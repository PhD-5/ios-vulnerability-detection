## 静态检测引擎

输入Mach-O或 ipa文件，依据规则库，基于IDAPython，进行数据流分析以及规则匹配。

这次主要修改在于IDAPython中的逻辑处理。  
在原逻辑中，对idautils.Functions()中的所有方法进行解析。一个普通的几M的二进制，方法数能达到万级。因此，希望对需要解析的方法数做限制。  
观察规则库，对规则做分类。规则分为以下几类：
1. \_arc4random方法。由于是import的方法，在二进制中无方法实现体。因此，找该符号的引用。其引用出现的方法体为可疑方法体。简单的思路就是，其stub会出现在idautils.Functions()中，找stub的引用即可；
2. msg_Send类规则。这类规则可能需要匹配receiver、selector或者参数。分别找到三者被引用关系所在的方法体，求交集即可。需要注意的是，参数有些不可求。参数的类型可能比较复杂，可能被硬编码，可能来自于cstring段，可能是数值，等等。也可能是间接值。保守起见，可以放宽要求。可疑的方法体宜多不宜少。  

```
<Function name="_objc_msgSend" type="true"> 
    <Parameter>X1=setObject:forKey:</Parameter>
    <Parameter>X3=kCFStreamSSLAllowsExpiredCertificates</Parameter>
</Function>
```


上图所示的kCFStreamSSLAllowsExpiredCertificates，key值，是import的符号。

Binary类是对二进制中的一些数据段做解析的。
StaticAnalyzer类是根据规则库做可以方法体限定的。
余下的是原逻辑中做数据流解析的。
做对比实验，差别就在：
```
for func in Functions():
# for func in sta.to_be_analyzed:
```
为了判断准确性，分别生成检测报告，看结果是否完整。


附：
这是整个项目的静态检测引擎部分。  
详细设计你看学长的《iOS静态检测API详细设计.docx》。  
两种输出，xml和pdf。可以做模块测试。
做模块测试的时候，可以在IDA中分别执行新旧两个版本的脚本，生成结果文件（数据库文件）。然后调用WriteXml.java中的方法生成报告。对比检查。  
建议：
1. 其实所有逻辑都可以放到ida来做。没必要麻烦java。
2. 做切片分析，不要对整个方法体进行分析。比如，当规则为\_arc4random方法时，它出现是瞬时的，是直接引用，调用结束后不会有遗留。因为它不会被放在寄存器、内存中被传递（block例外）。因此不需要做整个方法体的数据流解析，找到引用处就可以了。而对msg_Send类的规则，做切片分析，判断对象存在的上下文。



