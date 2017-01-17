# _*_coding:utf-8_*_
from idaapi import *
import idc
import idautils
import re
import sqlite3
import json
import sys

reload(sys)
sys.setdefaultencoding('utf-8')


class Parser():
    def __init__(self):
        self.cur_fun = None
        self.fun_start = None
        self.fun_end = None
        self.cur_fun_items = None

        self.conn = sqlite3.connect('bl.db')
        self.conn.execute('''CREATE TABLE IF NOT EXISTS BlTable
					(FUNC CHAR(100),
					BL CHAR(50),
					REGS CHAR(200));''')

    def parse_MOV(self, addr, regs):
        des = idc.GetOpnd(addr, 0)
        src = idc.GetOpnd(addr, 1)
        # ～change W to X , ignore W
        if des.startswith('W'):
            des = 'X' + des[1:]

        if src.startswith('#'):  # MOV X0 #0Xffff
            regs[des] = src[1:].encode('utf-8').strip()
        else:
            if src in regs:
                regs[des] = regs[src]
            elif des in regs:
                regs[des] = 'Unknown'

    def parse_STR(self, addr, regs):
        src = idc.GetOpnd(addr, 0)
        des = idc.GetOpnd(addr, 1)[1:-1]
        if src in regs:
            regs[des] = regs[src]
        else:
            regs[des] = 'Unknown'

    def parse_LDR(self, addr, regs):
        des = idc.GetOpnd(addr, 0)
        # ～change W to X , ignore W
        if des.startswith('W'):
            des = 'X' + des[1:]

        asm = idc.GetDisasm(addr)

        pat5 = re.compile(r'.+"(.+)".*')  # ~LDR X1, =sel_defaultManager; "defaultManager"  ""indicate the value
        pat = re.compile(r'.+;(.+)\*(.+)')  # ~LDR X1 = ......  ;NSObject *object
        pat1 = re.compile(
            r'.+#(classRef|selRef)_(.+)@PAGEOFF]')  # ~LDR  X0, [X23,#classRef_NSNumber@PAGEOFF]  X0 is NSNumber
        pat6 = re.compile(r'.+#_(.+)_ptr')  # !  LDR  X8, #_kSecAttrAccount_ptr@PAG X8=kSecAttrAccount
        pat2 = re.compile(r'.+\[(.+)\]')  # ~  LDR X3, [X8]   similar with MOV
        pat3 = re.compile(r'.+=_OBJC_CLASS_\$_(.+)')  # ~LDR  X0, =_OBJC_CLASS_$_NSMutableURLRequest
        pat4 = re.compile(r'.+=(.+)')  # ~LDR  X8, =_kCFStreamSSLAllowsAnyRoot

        m5 = pat5.match(asm)
        m = pat.match(asm)
        m1 = pat1.match(asm)
        m6 = pat6.match(asm)
        m2 = pat2.match(asm)
        m3 = pat3.match(asm)
        m4 = pat4.match(asm)

        if m5:
            regs[des] = unicode(m5.group(1), errors='ignore')
        elif m:
            content = m.group(1)
            content.strip()
            regs[des] = content
        elif m1:
            content = m1.group(2)
            if content.endswith('_'):
                content = content[:-1]
            regs[des] = content
        elif m6:
            regs[des] = m6.group(1)
        elif m2:
            src = m2.group(1)
            if src in regs:
                regs[des] = regs[src]
            else:
                regs[des] = 'Unknown'
        elif m3:
            regs[des] = m3.group(1)
        elif m4:
            regs[des] = m4.group(1)
        else:
            regs[des] = 'Unknown'

    def parse_B(self, func_name, addr, regs):
        ignore_list = ["_objc_autorelease", "_objc_release", "_objc_retain", "___stack_chk_fail"]
        imp_msg = ["_objc_msgSend", "_objc_msgSendSuper2", "_objc_msgSendSuper2_stret", "_objc_msgSend_stret"]
        lable = idc.GetOpnd(addr, 0)
        for item in ignore_list:
            if item in lable:
                return
        reg_info = json.dumps(regs)
        value = (func_name, lable, reg_info,)
        self.conn.execute("INSERT INTO BlTable VALUES (?,?,?)", value)
        '''
		if lable in imp_msg:
			value = (func_name, lable, reg_info, )
			conn.execute("INSERT INTO BlTable VALUES (?,?,?)",value)
		else:
			value = (func_name, lable, )
			conn.execute("INSERT INTO BlTable (FUNC, BL) VALUES (?,?)", value)
		'''
        self.conn.commit()

    def parse_Other(self, addr, regs):
        asm = idc.GetDisasm(addr)
        des = idc.GetOpnd(addr, 0)
        pat = re.compile(r'.+; "(.+)"')
        m = pat.match(asm)
        if m:
            str = m.group(1)
            str = unicode(str, errors='ignore')
            regs[des] = str

    def getX0FromFuncName(self, func_name):
        pat = re.compile(r".+\[(\S+).+")
        m = pat.match(func_name)
        if m:
            return m.group(1)
        else:
            return 'Unknown'

    def analysis_Xt(self, addr, xt, start_addr):
        if idc.GetMnem(addr) == 'LDR':
            return parse_LDR(addr)

    def main(self):

        for func in Functions():

            if idc.SegName(func) == '__stubs':
                continue

            func_name = idc.GetFunctionName(func)
            print 'start parse ', func_name

            # if func_name != '-[JailbreakDetectionVC isJailbroken]':
            #     continue

            self.cur_fun = func
            self.fun_start = idc.GetFunctionAttr(func, FUNCATTR_START)
            self.fun_end = idc.GetFunctionAttr(func, FUNCATTR_END)
            self.cur_fun_items = list(idautils.FuncItems(func))

            regs = dict()
            regs['X0'] = self.getX0FromFuncName(func_name)
            regs['X1'] = 'Unknown'

            self._parse(regs, 0)

        self.conn.close()

    def _parse(self, regs_dict, cur_index):

        for index in range(cur_index, len(self.cur_fun_items)):
            cur_addr = self.cur_fun_items[index]

            # parse cur addr
            ins = idc.GetMnem(cur_addr)

            if ins == 'MOV':
            	self.parse_MOV(cur_addr, regs_dict)
            elif ins == 'LDR' or ins == 'LDUR':
            	self.parse_LDR(cur_addr, regs_dict)
            elif ins == 'B' or ins == 'BL' or ins == 'BX' or ins == 'BLX' or ins == 'BLR':
            	self.parse_B(idc.GetFunctionName(self.cur_fun), cur_addr, regs_dict)
            elif ins == 'STR' or ins == 'STUR':
            	self.parse_STR(cur_addr, regs_dict)
            else:
            	self.parse_Other(cur_addr, regs_dict)

            for ref in idautils.CodeRefsFrom(cur_addr, 0):
                if ref > self.fun_start and ref < self.fun_end and ref > cur_addr:
                    next_index = self.cur_fun_items.index(ref)
                    new_dict = regs_dict.copy()
                    self._parse(new_dict, next_index)

            if len(list(idautils.CodeRefsFrom(cur_addr,0))) == len(list(idautils.CodeRefsFrom(cur_addr,1))):
                break


if __name__ == '__main__':
    parser = Parser()
    parser.main()
    Exit(0)


