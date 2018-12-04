package com.bupt.ios.report;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bupt.ios.commonData.CommonData;
import com.bupt.ios.commonData.ProjectParameters;
import com.bupt.ios.rule.SecRule;
import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class WritePdf {

	private static int MESSAGE_FONT_SIZE = 10;
	private static float TEXT_SPACING_BEFORE = 5f;// SpacingBefore
	private static float TITLE_SPACING_BEFORE = 10f;// SpacingBefore
	private static String fileSeparator = System.getProperty("file.separator");
	private static BaseFont baseFont;
	private static Font title0_font;
	private static Font title1_font;
	private static Font title2_font;
	private static Font messagetitle_font;
	private static Font message_font;
	private static final Map<Integer, String> riskLevel;

	static {
		try {
			baseFont = BaseFont.createFont(
					System.getProperty("user.dir") + fileSeparator + "conf" + fileSeparator + "SIMKAI.TTF",
					BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			title0_font = new Font(baseFont, 18, Font.BOLD, new Color(0, 0, 0));
			title1_font = new Font(baseFont, 14, Font.BOLD, new Color(0, 0, 0));
			title2_font = new Font(baseFont, 12, Font.BOLD, new Color(0, 0, 0));
			messagetitle_font = new Font(baseFont, MESSAGE_FONT_SIZE, Font.BOLD, new Color(0, 0, 0));
			message_font = new Font(baseFont, MESSAGE_FONT_SIZE, Font.NORMAL, new Color(0, 0, 0));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		riskLevel = new HashMap<Integer, String>();
		riskLevel.put(1, "低");
		riskLevel.put(2, "中");
		riskLevel.put(3, "高");
	}

	public WritePdf() {
		// TODO Auto-generated constructor stub
	}

	public static Paragraph genText(String title_lable, int level) {
		Paragraph p;
		switch (level) {
		case 0:
			p = new Paragraph(title_lable, title0_font);
			p.setAlignment(1);
			p.setSpacingAfter(10f);
			p.setSpacingBefore(TITLE_SPACING_BEFORE);
			break;
		case 1:
			p = new Paragraph(title_lable, title1_font);
			p.setSpacingAfter(10f);
			p.setSpacingBefore(TITLE_SPACING_BEFORE);
			break;
		case 2:
			p = new Paragraph(title_lable, title2_font);
			p.setSpacingBefore(TITLE_SPACING_BEFORE);
			break;
		case 4:
			p = new Paragraph(title_lable, message_font);
			p.setSpacingBefore(TEXT_SPACING_BEFORE);
			break;
		default:
			p = new Paragraph("Error title level", title0_font);
			break;
		}
		return p;
	}

	public static PdfPCell genCell(String text) {
		PdfPCell cell = new PdfPCell(new Paragraph(text, message_font));
		cell.setBackgroundColor(new Color(0xF0, 0xFF, 0xFF));
		return cell;
	}

	public static void write() throws DocumentException {

		try {
			String reportpath = ProjectParameters.getReportPath();
			File pdfFile = new File(reportpath + fileSeparator + ProjectParameters.getCurrentime() + ".pdf");
			if (!pdfFile.exists()) {
				pdfFile.createNewFile();
			}

			Document document = new Document(PageSize.A4);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

			document.open();
			document.addTitle("report");
			document.addAuthor("softsec");
			document.addSubject("This pdf is the report of this ipa.");
			document.addKeywords("softsec");
			document.addCreator("softsec");
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));

			// 封面
//			Font font = new Font(baseFont, 32, Font.BOLD, new Color(0, 0, 0));
//			Paragraph softsec = new Paragraph("CHINATELECOM", font);
//			softsec.setAlignment(2);
//			document.add(softsec);

			String filename = ProjectParameters.getAppName();
			Paragraph report = new Paragraph(filename + "缺陷扫描报告",
					new Font(baseFont, 20, Font.NORMAL, new Color(0, 0, 0)));
			report.setAlignment(2);
			document.add(report);

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Paragraph time = new Paragraph(df.format(new Date()),
					FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, new Color(0, 0, 0)));
			time.setAlignment(2);
			document.add(time);

			// 扫描报告
			Chapter chapter1 = new Chapter(genText("扫描报告", 0), 1);
			chapter1.setNumberDepth(0);
			// 第一节：基本信息
			Section section1 = chapter1.addSection(genText("基本信息", 1), 1);
			Section section11 = section1.addSection(genText("应用程序基本信息", 2), 2);
			section11.add(genText("应用名：" + ProjectParameters.getAppName(), 4));
			section11.add(genText("输入文件类型：Mach-O", 4));
//			section11.add(genText("加固状态：已脱壳", 4));

			Section section12 = section1.addSection(genText("测试说明", 2), 2);
			section12.add(genText("测试环境：自动化缺陷扫描实验室", 4));
			section12.add(genText("测试方法：使用缺陷扫描方法，配合静态扫描和规则库，发现应用程序可能存在的缺陷，并提出相关建议。", 4));
			section12.add(genText("缺陷扫描：为iOS提供一站式自动化缺陷扫描。", 4));

			// 第二节：调用信息
			Section section2 = chapter1.addSection(genText("调用信息", 1), 1);
			// 2.1 不安全API
			Section section21 = section2.addSection(genText("不安全API调用\n\n", 2), 2);

			for (String funcname : ReportSet.getRe1().keySet()) {
				List<SecRule> rules = ReportSet.getRe1().get(funcname);
				for (SecRule rule : rules) {
					float[] widths = { 0.25f, 0.75f };
					PdfPTable table = new PdfPTable(2);
					table.setWidths(widths);
					table.setHorizontalAlignment(Element.ALIGN_LEFT);
					table.addCell(genCell("函数名"));
					table.addCell(genCell(funcname));
					table.addCell(genCell("规则ID"));
					table.addCell(genCell(rule.getRuleId()));
					table.addCell(genCell("规则名称"));
					table.addCell(genCell(rule.getRuleName()));
					table.addCell(genCell("规则描述"));
					table.addCell(genCell(rule.getDescription()));
					table.addCell(genCell("缓解措施"));
					table.addCell(genCell(rule.getSolution()));
					table.addCell(genCell("危险等级"));
					table.addCell(genCell(riskLevel.get(rule.getRiskLevel())));

					section21.add(table);
					section21.add(new Paragraph("\n", message_font));
				}
			}

			// 2.2 安全保护措施
			Section section22 = section2.addSection(genText("已实施的安全保护措施\n\n", 2), 2);
			for (String funcname : ReportSet.getProtectInfoMap().keySet()) {
				List<SecRule> rules = ReportSet.getProtectInfoMap().get(funcname);
				for (SecRule rule : rules) {
					float[] widths = { 0.25f, 0.75f };
					PdfPTable table = new PdfPTable(2);
					table.setWidths(widths);
					table.setHorizontalAlignment(Element.ALIGN_LEFT);
					table.addCell(genCell("函数名"));
					table.addCell(genCell(funcname));
					table.addCell(genCell("规则ID"));
					table.addCell(genCell(rule.getRuleId()));
					table.addCell(genCell("规则名称"));
					table.addCell(genCell(rule.getRuleName()));
					table.addCell(genCell("规则描述"));
					table.addCell(genCell(rule.getDescription()));
					table.addCell(genCell("具体措施"));
					table.addCell(genCell(rule.getSolution()));

					section22.add(table);
					section22.add(new Paragraph("\n", message_font));
				}
			}
			
			// 2.3 建议实施的安全保护措施
			Section section23 = section2.addSection(genText("建议实施的安全保护措施\n\n", 2), 2);
			for(SecRule rule : CommonData.getRulePool2()){
				float[] widths = { 0.25f, 0.75f };
				PdfPTable table = new PdfPTable(2);
				table.setWidths(widths);
				table.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(genCell("规则ID"));
				table.addCell(genCell(rule.getRuleId()));
				table.addCell(genCell("规则名称"));
				table.addCell(genCell(rule.getRuleName()));
				table.addCell(genCell("规则描述"));
				table.addCell(genCell(rule.getDescription()));
				table.addCell(genCell("具体实施"));
				table.addCell(genCell(rule.getSolution()));
				table.addCell(genCell("重要等级"));
				table.addCell(genCell(riskLevel.get(rule.getRiskLevel())));

				section23.add(table);
				section23.add(new Paragraph("\n", message_font));
			}

			document.add(chapter1);
			document.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

//	public static void main(String argv[]) throws FileNotFoundException, DocumentException {
//		WritePdf.write();
//	}

}
