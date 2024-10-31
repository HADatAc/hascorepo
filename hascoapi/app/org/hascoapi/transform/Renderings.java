package org.hascoapi.transform;

//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.PdfWriter;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.*;
import org.hascoapi.vocabularies.VSTOI;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Renderings {

	/*
	 * private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
	 * Font.BOLD);
	 * private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	 * Font.NORMAL, BaseColor.RED);
	 * private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
	 * Font.BOLD);
	 * private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	 * Font.BOLD);
	 * private static Font smallNormal = new Font(Font.FontFamily.TIMES_ROMAN, 12,
	 * Font.NORMAL);
	 */

	public static String toString(String uri, int width) {
		Instrument instr = Instrument.find(uri);
		if (instr == null) {
			return "";
		}
		String str = "";

		str += centerText(instr.getHasShortName(), width) + "\n";

		str += "\n";
		if (instr.getSlotElements() != null) {
			for (SlotElement slotElement : instr.getSlotElements()) {
				if (slotElement instanceof ContainerSlot) {
					ContainerSlot containerSlot = (ContainerSlot)slotElement;
					Detector detector = containerSlot.getDetector();
					if (detector == null) {
						str += " " + containerSlot.getHasPriority() + ".  \n  ";
					} else {
						String content = "";
						//System.out.println(detector.toString());
						if (detector != null && detector.getDetectorStem() != null && detector.getDetectorStem().getHasContent() != null) {
							content = detector.getDetectorStem().getHasContent();
						}
						str += " " + containerSlot.getHasPriority() + ". " + content + " ";
						Codebook codebook = detector.getCodebook();
						if (codebook != null && codebook.getCodebookSlots() != null) {
							List<CodebookSlot> slots = codebook.getCodebookSlots();
							if (slots != null && slots.size() > 0) {
								for (CodebookSlot slot : slots) {
									if (slot.getResponseOption() != null) {
										ResponseOption responseOption = slot.getResponseOption();
										str += " " + responseOption.getHasContent() + "( )  ";
									}
								}
							}
						}
					}
				}
				str += "\n\n";
			}
			str += "\n";
		}
		return str;
	}

	private static String centerText(String str, int width) {
		if (str == null) {
			str = "";
		}
		if (str.length() > width) {
			return str;
		}
		int left = (width - str.length()) / 2;
		StringBuffer newStr = new StringBuffer();
		for (int i = 0; i < left; i++) {
			newStr.append(" ");
		}
		newStr.append(str);
		return newStr.toString();
	}

	private static List<String> breakString(String str, int width) {
		List<String> lines = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(str);
		String newLine = "";
		String nextWord = "";
		while (st.hasMoreTokens()) {
			nextWord = st.nextToken();
			if (nextWord.length() >= width) {
				if (newLine.equals("")) {
					newLine = nextWord;
				} else {
					newLine = newLine + " " + nextWord;
				}
				lines.add(newLine);
				newLine = "";
			} else if (newLine.length() + nextWord.length() > width) {
				lines.add(newLine);
				newLine = nextWord;
			} else {
				if (newLine.equals("")) {
					newLine = nextWord;
				} else {
					newLine = newLine + " " + nextWord;
				}
			}

		}
		if (newLine.length() > 0) {
			lines.add(newLine);
		}
		return lines;
	}

	private static String runtimeRendering(String rendering, int page) {
		//System.out.println("Rendering: [" + rendering + "]  page: [" + page + "]" );
        if (rendering.indexOf(Constants.META_VARIABLE_PAGE) == -1) {
            return rendering;
        }
		String pageStr = String.valueOf(page);
        String str =  rendering.replaceAll(Constants.META_VARIABLE_PAGE,pageStr);
		return str;	
	}

	private static String headerHTML(Instrument instr, int page) {
		Annotation topLeftAnnotation = Annotation.findByContainerAndPosition(instr.getUri(),VSTOI.PAGE_TOP_LEFT);
		Annotation topCenterAnnotation = Annotation.findByContainerAndPosition(instr.getUri(),VSTOI.PAGE_TOP_CENTER);
		Annotation topRightAnnotation = Annotation.findByContainerAndPosition(instr.getUri(),VSTOI.PAGE_TOP_RIGHT);
		Annotation lineBelowTopAnnotation = Annotation.findByContainerAndPosition(instr.getUri(),VSTOI.PAGE_LINE_BELOW_TOP);

 		String topLeft = "";
		if (topLeftAnnotation != null) {
			topLeft = topLeftAnnotation.getRendering();
			topLeft = runtimeRendering(topLeft,page);
		}
		String topCenter = "";
		if (topCenterAnnotation != null) {
			topCenter = topCenterAnnotation.getRendering();
			topCenter = runtimeRendering(topCenter,page);
		}
		String topRight = "";
		if (topRightAnnotation != null) {
			topRight = topRightAnnotation.getRendering();
			topRight = runtimeRendering(topRight,page);
		}
		String lineBelowTop = "";
		if (lineBelowTopAnnotation != null) {
			lineBelowTop = lineBelowTopAnnotation.getRendering();
			lineBelowTop = runtimeRendering(lineBelowTop,page);
		}

		return "<table id=\"tbl1\"> " +
				"  <tr id=\"tr1\"> " +
				"	  <td id=\"leftcell\">" + topLeft + "</td> " +
				"	  <td id=\"centercell\">" + topCenter + "</td> " +
				"	  <td id=\"rightcell\">" + topRight + "</td> " +
				"  </tr>" +
				"</table> " +
				"<br>" +
				lineBelowTop + "<br>" +
				"<br>\n";
	}

	private static String footerHTML(Instrument instr, int page) {

		Annotation bottomLeftAnnotation = Annotation.findByContainerAndPosition(instr.getUri(),VSTOI.PAGE_BOTTOM_LEFT);
		Annotation bottomCenterAnnotation = Annotation.findByContainerAndPosition(instr.getUri(),VSTOI.PAGE_BOTTOM_CENTER);
		Annotation bottomRightAnnotation = Annotation.findByContainerAndPosition(instr.getUri(),VSTOI.PAGE_BOTTOM_RIGHT);
		Annotation lineAboveBottomAnnotation = Annotation.findByContainerAndPosition(instr.getUri(),VSTOI.PAGE_LINE_ABOVE_BOTTOM);

		String lineAboveBottom = "";
		if (lineAboveBottomAnnotation != null) {
			lineAboveBottom = lineAboveBottomAnnotation.getRendering();
			lineAboveBottom = runtimeRendering(lineAboveBottom,page);
		}
 		String bottomLeft = "";
		if (bottomLeftAnnotation != null) {
			bottomLeft = bottomLeftAnnotation.getRendering();
			bottomLeft = runtimeRendering(bottomLeft,page);
		}
		String bottomCenter = "";
		if (bottomCenterAnnotation != null) {
			bottomCenter = bottomCenterAnnotation.getRendering();
			bottomCenter = runtimeRendering(bottomCenter,page);
		}
		String bottomRight = "";
		if (bottomRightAnnotation != null) {
			bottomRight = bottomRightAnnotation.getRendering();
			bottomRight = runtimeRendering(bottomRight,page);
		}

		return lineAboveBottom + "<br><br>" + 
				"<table id=\"tbl1\"> " +
				"  <tr id=\"tr1\"> " +
				"	  <td id=\"leftcell\">" + bottomLeft + "</td> " +
				"	  <td id=\"centercell\">" + bottomCenter + "</td> " +
				"	  <td id=\"rightcell\">" + bottomRight + "</td> " +
				"  </tr>" +
				"</table> " +
				"<br><br><br><br>";
	}

	private static String styleHTML() {
		return "<style>\n" +
				"table, tr, td {\n" +
				"  border: 1px solid;\n" +
				"  border-collapse: collapse;\n" +
				"  padding: 5px;\n" +
				"  width: 100%;\n" +
				"}\n" +
				"tr:nth-child(even) {\n" +
				"  background-color: #f2f2f2;\n" +
				"}\n" +
				"#tbl1, #tr1, #leftcell, #centercell, #rightcell {\n" +
				"  border: 0px solid;\n" +
				"  border-collapse: collapse;\n" +
				"  padding: 5px;\n" +
				"  white-space: nowrap;\n" +
				"}\n" +
				"#leftcell {\n" +
				"	text-align: left;\n" +
				"}\n" +
				"#centercell {\n" +
				"   padding-right: 80px;\n" +
				"	text-align: center;\n" +
				"   padding-left: 80px;\n" +
				"}\n" +
				"#rightcell {\n" +
				"	text-align: right;\n" +
				"}\n" +
				"</style>\n";
	}

	private static String printPage(Instrument instr, int page) {
		String html = "";

		// PRINT HEADER
		if (page == 1) {
			html += headerHTML(instr,page);
			html += "<br>\n";
		}

		int first = 0;
		int last = 0;
		int currentPageSize = 0;
		if (page == 1) {
			currentPageSize = 25;
			first = 1;
			if (instr.getSlotElements() != null && instr.getSlotElements().size() > 25) {
				last = 25;
			} else {
				if (instr.getSlotElements() != null) {
					last = instr.getSlotElements().size();
				} else {
					last = 0;
				}
			}
		} else {
			currentPageSize = 27;
			int past = 25 + ((page - 2) * 27);
			first = past + 1;
			int rest = instr.getSlotElements().size() - past;
			if (rest > currentPageSize) {
				last = past + 27;
			} else {
				last = past + rest;
			}
		}

		// System.out.println("Page: " + page);
		// System.out.println(" First: " + first);
		// System.out.println(" Last: " + last);
		// System.out.println(" CurrentPageSize: " + currentPageSize);
		// System.out.println("");
		html += "<table>\n";
		if (instr.getSlotElements() == null || instr.getSlotElements().size() <= 0) {
			html += "<p>EMPTY TABLE</p>";
		} else {
			// System.out.println("Renderings.java: total containerSlots: " +
			// instr.getSlotElements().size());
			for (int element = first - 1; element < last; element++) {
				SlotElement slotElement = instr.getSlotElements().get(element);
				if (slotElement instanceof ContainerSlot) {
					ContainerSlot containerSlot = (ContainerSlot)slotElement;
					Detector detector = containerSlot.getDetector();
					if (detector == null) {
						if (containerSlot.getHasPriority() != null) {
							html += "<tr><td>" + containerSlot.getHasPriority() + ".</tr></td>\n";
						}
					} else {
						String content = "";
						//System.out.println(detector.toString());
						if (detector != null && detector.getDetectorStem() != null && detector.getDetectorStem().getHasContent() != null) {
							content = detector.getDetectorStem().getHasContent();
						}
						html += "<tr>";
						html += "<td>" + containerSlot.getHasPriority() + ". " + content + "</td>";
						Codebook codebook = detector.getCodebook();
						if (codebook != null) {
							List<CodebookSlot> slots = codebook.getCodebookSlots();
							if (slots != null && slots.size() > 0) {
								for (CodebookSlot slot : slots) {
									if (slot.getResponseOption() != null) {
										ResponseOption responseOption = slot.getResponseOption();
										if (responseOption != null && responseOption.getHasContent() != null) {
											html += "<td>" + responseOption.getHasContent() + "</td>";
										}
									}
								}
							}
						}
						html += "</tr>\n";
					}
				}
			}
		}
		html += "</table>\n";

		// FILL THE REST OF THE PAGE BLANK
		for (int aux = 0; aux + last <= currentPageSize; aux++) {
			html += "<br>";
		}

		// PRINT FOOTER
		html += footerHTML(instr, page);

		return html;
	}

	public static String toHTML(String uri, int width) {

		// System.out.println("Rendering.java: rendering [" + uri + "]");

		Instrument instr = Instrument.find(uri);

		String html = "";

		html += "<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				styleHTML() +
				"</head>\n" +
				"<body>\n";

		if (instr == null) {
			html += "<p>EMPTY INSTRUMENT RENDERING</P>";
			html += "</body>\n" +
					"</html>";
			return html;
		}

		// PRINT ITEMS
		int elements = 0;
		int totPages = 0;
		if (instr.getSlotElements() == null) {
			html += printPage(instr, 1);
		} else {

			// total of pages
			if (instr.getSlotElements().size() <= 25) {
				totPages = 1;
			} else {
				totPages = 1 + ((instr.getSlotElements().size() - 25) / 27);
				if (((instr.getSlotElements().size() - 25) % 27) > 0) {
					totPages = totPages + 1;
				}
			}

			// System.out.println("Rendering.java: total pages [" + totPages + "]");

			// print pages
			for (int page = 1; page <= totPages; page++) {
				// System.out.println("Rendering.java: print page [" + page + "]");
				html += printPage(instr, page);
			}
		}

		html += "</body>\n" +
				"</html>";
		return html;
	}

	public static ByteArrayOutputStream toPDF(String uri, int width) {
		Instrument instr = Instrument.find(uri);
		if (instr == null) {
			return null;
		}
		String fileName = "https://example.com/" + instr.getHasShortName() + "_V" + instr.getHasVersion() + ".pdf";

		Document document = Jsoup.parse(Renderings.toHTML(uri, width), "UTF-8");
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ITextRenderer renderer = new ITextRenderer();
			SharedContext sharedContext = renderer.getSharedContext();
			sharedContext.setPrint(true);
			sharedContext.setInteractive(false);
			renderer.setDocumentFromString(document.html(), fileName);
			renderer.layout();
			renderer.createPDF(outputStream);
			return outputStream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * Document document = new Document();
		 * try {
		 * PdfWriter.getInstance(document, new FileOutputStream(fileName));
		 * } catch (DocumentException e) {
		 * e.printStackTrace();
		 * } catch (FileNotFoundException e) {
		 * e.printStackTrace();
		 * }
		 * 
		 * document.open();
		 * Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
		 * Chunk chunk = new Chunk(Renderings.toString(uri,width), font);
		 * 
		 * try {
		 * //document.add(chunk);
		 * Renderings.addTitlePage(document, instr);
		 * } catch (DocumentException e) {
		 * e.printStackTrace();
		 * }
		 * document.close();
		 */

		return null;
	}

	/*
	 * private static void addTitlePage(Document document, Instrument instr)
	 * throws DocumentException {
	 * Paragraph preface = new Paragraph();
	 * // We add one empty line
	 * addEmptyLine(preface, 1);
	 * // Lets write a big header
	 * preface.add(new Paragraph(instr.getHasShortName(), catFont));
	 * 
	 * addEmptyLine(preface, 1);
	 * // Will create: Report generated by: _name, _date
	 * //preface.add(new Paragraph("Report generated by: " +
	 * System.getProperty("user.name") + smallBold));
	 * //addEmptyLine(preface, 3);
	 * preface.add(new Paragraph(instr.getHasInstruction(), smallNormal));
	 * addEmptyLine(preface, 1);
	 * //preface.add(new
	 * Paragraph("This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-)."
	 * , redFont));
	 * 
	 * document.add(preface);
	 * // Start a new page
	 * document.newPage();
	 * }
	 * 
	 * private static void addEmptyLine(Paragraph paragraph, int number) {
	 * for (int i = 0; i < number; i++) {
	 * paragraph.add(new Paragraph(" "));
	 * }
	 * }
	 */

}
