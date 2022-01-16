package org.grits.toolbox.glytoucan.registry.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanFile;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanInformation;
import org.grits.toolbox.glytoucan.registry.client.om.ImageInformation;

public class ExcelReport
{
    private static final Double IMAGE_CELL_WIDTH_FACTOR = 36.55D;
    private static final Double IMAGE_CELL_HEIGHT_FACTOR = 0.76D;

    private String m_reportFolder = null;
    private Workbook m_workbook = null;
    private CellStyle m_cellStyleHeadline = null;
    private CellStyle m_cellStyleTextMiddle = null;
    private Font m_fontHeadline = null;
    private CreationHelper m_creationHelper = null;

    public void writeReport(List<GlycanFile> a_glycanFiles, String a_reportFolder)
            throws IOException
    {
        // create all images first
        this.createImages(a_glycanFiles);
        // create the workbook
        this.m_reportFolder = a_reportFolder;
        this.m_workbook = new XSSFWorkbook();
        this.m_creationHelper = this.m_workbook.getCreationHelper();

        this.createStylesAndFonts();
        // summary sheet
        Sheet t_sheet = this.m_workbook.createSheet("Summary");
        this.fillSummarySheet(t_sheet, a_glycanFiles);
        // image compare sheet
        t_sheet = this.m_workbook.createSheet("Image comparison");
        this.fillComparisonSheet(t_sheet, a_glycanFiles);
        // debug sheet
        t_sheet = this.m_workbook.createSheet("Debug");
        this.fillDebugSheet(t_sheet, a_glycanFiles);
        // write information to the file
        OutputStream t_fileOutputStream = new FileOutputStream(
                this.m_reportFolder + File.separator + "report.xlsx");
        this.m_workbook.write(t_fileOutputStream);
    }

    private void createImages(List<GlycanFile> a_glycanFiles)
    {
        ImageGeneration t_imageGeneration = new ImageGeneration();
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            // for each glycan in the file
            for (GlycanInformation t_glycan : t_glycanFile.getGlycans())
            {
                t_imageGeneration.createImages(t_glycan);
            }
        }
    }

    private void fillComparisonSheet(Sheet a_sheet, List<GlycanFile> a_glycanFiles)
    {
        this.setSheetImageColumnWidthComparison(a_sheet, a_glycanFiles);
        Drawing<?> drawing = a_sheet.createDrawingPatriarch();
        Row t_rowFirstInFile = null;
        Row t_rowCurrent = null;
        Integer t_counterRow = 0;
        Integer t_glycanCounter = 1;
        // headline
        this.writeHeadlineComparison(a_sheet.createRow(t_counterRow++));
        // for each file in the list
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            int t_firstRowIndex = t_counterRow;
            Cell t_cell = null;
            // for each glycan in the file
            for (GlycanInformation t_glycan : t_glycanFile.getGlycans())
            {
                t_rowCurrent = a_sheet.createRow(t_counterRow++);
                if (t_rowFirstInFile == null)
                {
                    t_rowFirstInFile = t_rowCurrent;
                }
                // write information - glycan number
                t_cell = t_rowCurrent.createCell(1, CellType.NUMERIC);
                t_cell.setCellStyle(this.m_cellStyleTextMiddle);
                t_cell.setCellValue(t_glycanCounter++);
                // GlyTouCan ID
                if (t_glycan.getGlyTouCanId() != null)
                {
                    t_cell = t_rowCurrent.createCell(2, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getGlyTouCanId());
                    t_cell.setCellStyle(this.m_cellStyleTextMiddle);
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(2, CellType.BLANK);
                }
                // images
                t_cell = t_rowCurrent.createCell(3, CellType.BLANK);
                if (t_glycan.getImageGWS() != null)
                {
                    Integer t_height = t_glycan.getImageGWS().getImage().getHeight();
                    t_rowCurrent.setHeightInPoints((int) (t_height * IMAGE_CELL_HEIGHT_FACTOR) + 1);
                    this.addImage(drawing, t_glycan.getImageGWS(), t_counterRow - 1, 3);
                }
                t_cell = t_rowCurrent.createCell(4, CellType.BLANK);
                if (t_glycan.getImageGlycoCT() != null)
                {
                    Integer t_height = t_glycan.getImageGlycoCT().getImage().getHeight();
                    t_rowCurrent.setHeightInPoints((int) (t_height * IMAGE_CELL_HEIGHT_FACTOR) + 1);
                    this.addImage(drawing, t_glycan.getImageGlycoCT(), t_counterRow - 1, 4);
                }
                t_cell = t_rowCurrent.createCell(5, CellType.BLANK);
                if (t_glycan.getImageWURCS() != null)
                {
                    Integer t_height = t_glycan.getImageWURCS().getImage().getHeight();
                    t_rowCurrent.setHeightInPoints((int) (t_height * IMAGE_CELL_HEIGHT_FACTOR) + 1);
                    this.addImage(drawing, t_glycan.getImageWURCS(), t_counterRow - 1, 5);
                }
                t_cell = t_rowCurrent.createCell(6, CellType.BLANK);
                if (t_glycan.getImageWURCSAfterRegistration() != null)
                {
                    Integer t_height = t_glycan.getImageWURCSAfterRegistration().getImage()
                            .getHeight();
                    t_rowCurrent.setHeightInPoints((int) (t_height * IMAGE_CELL_HEIGHT_FACTOR) + 1);
                    this.addImage(drawing, t_glycan.getImageWURCSAfterRegistration(),
                            t_counterRow - 1, 6);
                }
                t_cell = t_rowCurrent.createCell(7, CellType.BLANK);
                if (t_glycan.getImageGlycoCTAfterRegistration() != null)
                {
                    Integer t_height = t_glycan.getImageGlycoCTAfterRegistration().getImage()
                            .getHeight();
                    t_rowCurrent.setHeightInPoints((int) (t_height * IMAGE_CELL_HEIGHT_FACTOR) + 1);
                    this.addImage(drawing, t_glycan.getImageGlycoCTAfterRegistration(),
                            t_counterRow - 1, 7);
                }
                t_cell = t_rowCurrent.createCell(8, CellType.BLANK);
                if (t_glycan.getImageGWSAfterRegistration() != null)
                {
                    Integer t_height = t_glycan.getImageGWSAfterRegistration().getImage()
                            .getHeight();
                    t_rowCurrent.setHeightInPoints((int) (t_height * IMAGE_CELL_HEIGHT_FACTOR) + 1);
                    this.addImage(drawing, t_glycan.getImageGWSAfterRegistration(),
                            t_counterRow - 1, 8);
                }
            }
            // write the file name and span row
            a_sheet.addMergedRegion(new CellRangeAddress(t_firstRowIndex, t_counterRow, 0, 0));
            t_cell = t_rowFirstInFile.createCell(0, CellType.STRING);
            t_cell.setCellValue(t_glycanFile.getFileName());
            t_glycanCounter = 1;
            t_rowFirstInFile = null;
        }
        // make columns wide enough to fit the text for file, GlyTouCan ID and
        // glycan number
        a_sheet.autoSizeColumn(0, true);
        a_sheet.autoSizeColumn(1);
        a_sheet.autoSizeColumn(2);
    }

    private void setSheetImageColumnWidthComparison(Sheet a_sheet, List<GlycanFile> a_glycanFiles)
    {
        Integer t_maxImageWidthD = 100;
        Integer t_maxImageWidthE = 100;
        Integer t_maxImageWidthF = 100;
        Integer t_maxImageWidthG = 100;
        Integer t_maxImageWidthH = 100;
        Integer t_maxImageWidthI = 100;
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            // for each glycan in the file
            for (GlycanInformation t_glycan : t_glycanFile.getGlycans())
            {
                if (t_glycan.getImageGWS() != null)
                {
                    Integer t_width = t_glycan.getImageGWS().getImage().getWidth();
                    if (t_width > t_maxImageWidthD)
                    {
                        t_maxImageWidthD = t_width;
                    }
                }
                if (t_glycan.getImageGlycoCT() != null)
                {
                    Integer t_width = t_glycan.getImageGlycoCT().getImage().getWidth();
                    if (t_width > t_maxImageWidthE)
                    {
                        t_maxImageWidthE = t_width;
                    }
                }
                if (t_glycan.getImageWURCS() != null)
                {
                    Integer t_width = t_glycan.getImageWURCS().getImage().getWidth();
                    if (t_width > t_maxImageWidthF)
                    {
                        t_maxImageWidthF = t_width;
                    }
                }
                if (t_glycan.getImageWURCSAfterRegistration() != null)
                {
                    Integer t_width = t_glycan.getImageWURCSAfterRegistration().getImage()
                            .getWidth();
                    if (t_width > t_maxImageWidthG)
                    {
                        t_maxImageWidthG = t_width;
                    }
                }
                if (t_glycan.getImageGlycoCTAfterRegistration() != null)
                {
                    Integer t_width = t_glycan.getImageGlycoCTAfterRegistration().getImage()
                            .getWidth();
                    if (t_width > t_maxImageWidthH)
                    {
                        t_maxImageWidthH = t_width;
                    }
                }
                if (t_glycan.getImageGWSAfterRegistration() != null)
                {
                    Integer t_width = t_glycan.getImageGWSAfterRegistration().getImage().getWidth();
                    if (t_width > t_maxImageWidthI)
                    {
                        t_maxImageWidthI = t_width;
                    }
                }
            }
        }
        a_sheet.setColumnWidth(3, (int) (t_maxImageWidthD * IMAGE_CELL_WIDTH_FACTOR));
        a_sheet.setColumnWidth(4, (int) (t_maxImageWidthE * IMAGE_CELL_WIDTH_FACTOR));
        a_sheet.setColumnWidth(5, (int) (t_maxImageWidthF * IMAGE_CELL_WIDTH_FACTOR));
        a_sheet.setColumnWidth(6, (int) (t_maxImageWidthG * IMAGE_CELL_WIDTH_FACTOR));
        a_sheet.setColumnWidth(7, (int) (t_maxImageWidthH * IMAGE_CELL_WIDTH_FACTOR));
        a_sheet.setColumnWidth(8, (int) (t_maxImageWidthI * IMAGE_CELL_WIDTH_FACTOR));
    }

    private void fillDebugSheet(Sheet a_sheet, List<GlycanFile> a_glycanFiles)
    {
        Row t_rowFirstInFile = null;
        Row t_rowCurrent = null;
        Integer t_counterRow = 0;
        Integer t_glycanCounter = 1;
        // headline
        this.writeHeadlineDebug(a_sheet.createRow(t_counterRow++));
        // for each file in the list
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            int t_firstRowIndex = t_counterRow;
            Cell t_cell = null;
            // for each glycan in the file
            for (GlycanInformation t_glycan : t_glycanFile.getGlycans())
            {
                // create the row and remember if it was the first for the
                // glycan file
                Integer t_column = 1;
                t_rowCurrent = a_sheet.createRow(t_counterRow++);
                if (t_rowFirstInFile == null)
                {
                    t_rowFirstInFile = t_rowCurrent;
                }
                // write information - glycan number
                t_cell = t_rowCurrent.createCell(t_column++, CellType.NUMERIC);
                t_cell.setCellStyle(this.m_cellStyleTextMiddle);
                t_cell.setCellValue(t_glycanCounter++);
                // GlyTouCan ID
                if (t_glycan.getGlyTouCanId() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellStyle(this.m_cellStyleTextMiddle);
                    t_cell.setCellValue(t_glycan.getGlyTouCanId());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // Failed
                t_cell = t_rowCurrent.createCell(t_column++, CellType.BOOLEAN);
                t_cell.setCellStyle(this.m_cellStyleTextMiddle);
                t_cell.setCellValue(t_glycan.isFailed());
                // Error message
                if (t_glycan.getError() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getError());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // ErrorInfo
                if (t_glycan.getErrorInfo() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(this.infoToString(t_glycan.getErrorInfo()));
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // Warning
                if (t_glycan.getWarnings() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(this.infoToString(t_glycan.getWarnings()));
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // GWS
                if (t_glycan.getGws() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getGws());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // GlycoCT
                if (t_glycan.getGlycoCt() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getGlycoCt());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // recoded
                if (t_glycan.getGlycoCtRecoded() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getGlycoCtRecoded());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // WURCS
                if (t_glycan.getWurcs() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getWurcs());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // WURCS
                if (t_glycan.getWurcsAfterRegistration() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getWurcsAfterRegistration());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // GlycoCT
                if (t_glycan.getGlycoCtAfterRegistration() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getGlycoCtAfterRegistration());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // GWS
                if (t_glycan.getGwsAfterRegistration() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getGwsAfterRegistration());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // GWS ordered
                if (t_glycan.getGwsOrdered() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getGwsOrdered());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
                // GWS
                if (t_glycan.getGwsOrderedAfterRegistration() != null)
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getGwsOrderedAfterRegistration());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(t_column++, CellType.BLANK);
                }
            }
            // write the file name and span row
            a_sheet.addMergedRegion(new CellRangeAddress(t_firstRowIndex, t_counterRow - 1, 0, 0));
            t_cell = t_rowFirstInFile.createCell(0, CellType.STRING);
            t_cell.setCellStyle(this.m_cellStyleTextMiddle);
            t_cell.setCellValue(t_glycanFile.getFileName());
            t_glycanCounter = 1;
            t_rowFirstInFile = null;
        }
        // make columns wide enough to fit the text for file, GlyTouCan ID and
        // glycan number
        a_sheet.autoSizeColumn(0, true);
        a_sheet.autoSizeColumn(1);
        a_sheet.autoSizeColumn(2);
        a_sheet.autoSizeColumn(3);
        a_sheet.setColumnWidth(4, 6000);
        a_sheet.setColumnWidth(5, 6000);
        a_sheet.setColumnWidth(6, 6000);
        a_sheet.setColumnWidth(7, 3000);
        a_sheet.setColumnWidth(8, 3000);
        a_sheet.setColumnWidth(9, 3000);
        a_sheet.setColumnWidth(10, 3000);
        a_sheet.setColumnWidth(11, 3000);
        a_sheet.setColumnWidth(12, 3000);
        a_sheet.setColumnWidth(13, 3000);
        a_sheet.setColumnWidth(14, 3000);
        a_sheet.setColumnWidth(15, 3000);
    }

    private String infoToString(List<String> a_stringList)
    {
        StringBuffer t_buffer = new StringBuffer("");
        for (String t_string : a_stringList)
        {
            t_buffer.append(t_string);
            t_buffer.append("\n");
        }
        return t_buffer.toString();
    }

    private void createStylesAndFonts()
    {
        // headline font
        this.m_fontHeadline = this.m_workbook.createFont();
        this.m_fontHeadline.setBold(true);
        this.m_fontHeadline.setFontHeightInPoints((short) 12);
        // headline style
        this.m_cellStyleHeadline = this.m_workbook.createCellStyle();
        this.m_cellStyleHeadline.setFillBackgroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        this.m_cellStyleHeadline.setFillPattern(FillPatternType.FINE_DOTS);
        this.m_cellStyleHeadline.setFont(this.m_fontHeadline);
        // text horizontal and veritcal middle aligned
        this.m_cellStyleTextMiddle = this.m_workbook.createCellStyle();
        this.m_cellStyleTextMiddle.setAlignment(HorizontalAlignment.CENTER);
        this.m_cellStyleTextMiddle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    private void fillSummarySheet(Sheet a_sheet, List<GlycanFile> a_glycanFiles)
    {
        this.setSheetImageColumnWidthSummary(a_sheet, a_glycanFiles);
        Drawing<?> drawing = a_sheet.createDrawingPatriarch();
        Row t_rowFirstInFile = null;
        Row t_rowCurrent = null;
        Integer t_counterRow = 0;
        Integer t_glycanCounter = 1;
        // headline
        this.writeHeadlineSummary(a_sheet.createRow(t_counterRow++));
        // for each file in the list
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            int t_firstRowIndex = t_counterRow;
            Cell t_cell = null;
            // for each glycan in the file
            for (GlycanInformation t_glycan : t_glycanFile.getGlycans())
            {
                // create the row and remember if it was the first for the
                // glycan file
                t_rowCurrent = a_sheet.createRow(t_counterRow++);
                if (t_rowFirstInFile == null)
                {
                    t_rowFirstInFile = t_rowCurrent;
                }
                // write information - glycan number
                t_cell = t_rowCurrent.createCell(1, CellType.NUMERIC);
                t_cell.setCellStyle(this.m_cellStyleTextMiddle);
                t_cell.setCellValue(t_glycanCounter++);
                // image / error
                t_cell = t_rowCurrent.createCell(2, CellType.BLANK);
                if (t_glycan.getImageGWS() != null)
                {
                    Integer t_height = t_glycan.getImageGWS().getImage().getHeight();
                    t_rowCurrent.setHeightInPoints((int) (t_height * IMAGE_CELL_HEIGHT_FACTOR) + 1);
                    this.addImage(drawing, t_glycan.getImageGWS(), t_counterRow - 1, 2);
                }
                // GlyTouCan ID
                if (t_glycan.getGlyTouCanId() != null)
                {
                    t_cell = t_rowCurrent.createCell(3, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getGlyTouCanId());
                    t_cell.setCellStyle(this.m_cellStyleTextMiddle);
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(3, CellType.BLANK);
                }
                // image
                if (t_glycan.getError() != null)
                {
                    t_cell = t_rowCurrent.createCell(4, CellType.STRING);
                    t_cell.setCellValue(t_glycan.getError());
                }
                else
                {
                    t_cell = t_rowCurrent.createCell(4, CellType.BLANK);
                    ImageInformation t_finalImage = t_glycan.findFinalImage();
                    if (t_finalImage != null)
                    {
                        Integer t_height = t_finalImage.getImage().getHeight();
                        t_rowCurrent
                                .setHeightInPoints((int) (t_height * IMAGE_CELL_HEIGHT_FACTOR) + 1);
                        this.addImage(drawing, t_finalImage, t_counterRow - 1, 4);
                    }
                }
            }
            // write the file name and span row
            a_sheet.addMergedRegion(new CellRangeAddress(t_firstRowIndex, t_counterRow - 1, 0, 0));
            t_cell = t_rowFirstInFile.createCell(0, CellType.STRING);
            t_cell.setCellStyle(this.m_cellStyleTextMiddle);
            t_cell.setCellValue(t_glycanFile.getFileName());
            t_glycanCounter = 1;
            t_rowFirstInFile = null;
        }
        // make columns wide enough to fit the text for file, GlyTouCan ID and
        // glycan number
        a_sheet.autoSizeColumn(0, true);
        a_sheet.autoSizeColumn(1);
        a_sheet.autoSizeColumn(3);
    }

    private void setSheetImageColumnWidthSummary(Sheet a_sheet, List<GlycanFile> a_glycanFiles)
    {
        Integer t_maxImageWidth = 100;
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            // for each glycan in the file
            for (GlycanInformation t_glycan : t_glycanFile.getGlycans())
            {
                if (t_glycan.getImageGWS() != null)
                {
                    Integer t_width = t_glycan.getImageGWS().getImage().getWidth();
                    if (t_width > t_maxImageWidth)
                    {
                        t_maxImageWidth = t_width;
                    }
                }

            }
        }
        a_sheet.setColumnWidth(2, (int) (t_maxImageWidth * IMAGE_CELL_WIDTH_FACTOR));
        t_maxImageWidth = 100;
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            // for each glycan in the file
            for (GlycanInformation t_glycan : t_glycanFile.getGlycans())
            {
                ImageInformation t_image = t_glycan.findFinalImage();
                if (t_image != null)
                {
                    Integer t_width = t_image.getImage().getWidth();
                    if (t_width > t_maxImageWidth)
                    {
                        t_maxImageWidth = t_width;
                    }
                }
            }
        }
        a_sheet.setColumnWidth(4, (int) (t_maxImageWidth * IMAGE_CELL_WIDTH_FACTOR));
    }

    private void addImage(Drawing<?> a_drawing, ImageInformation a_imageInfo, int a_row,
            int a_column)
    {
        // add picture data to this workbook
        Integer t_pictureIndex = a_imageInfo.getImageIndex();
        if (t_pictureIndex == null)
        {
            t_pictureIndex = this.m_workbook.addPicture(a_imageInfo.getImageData(),
                    Workbook.PICTURE_TYPE_JPEG);
        }
        // add a picture shape
        ClientAnchor t_anchor = this.m_creationHelper.createClientAnchor();
        // set top-left corner of the picture,
        // subsequent call of Picture#resize() will operate relative to it
        t_anchor.setCol1(a_column);
        t_anchor.setRow1(a_row);
        Picture t_picture = a_drawing.createPicture(t_anchor, t_pictureIndex);
        // auto-size picture relative to its top-left corner
        t_picture.resize();
    }

    private void writeHeadlineSummary(Row a_row)
    {
        // file
        Cell t_cell = a_row.createCell(0);
        t_cell.setCellValue("File");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // glycan in file
        t_cell = a_row.createCell(1);
        t_cell.setCellValue("Glycan Number");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // Image or error message
        t_cell = a_row.createCell(2);
        t_cell.setCellValue("Original glycan (CFG)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GlyTouCan ID
        t_cell = a_row.createCell(3);
        t_cell.setCellValue("GlyTouCan ID");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // Final image
        t_cell = a_row.createCell(4);
        t_cell.setCellValue("Registered glycan (SNFG)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);

    }

    private void writeHeadlineComparison(Row a_row)
    {
        // file
        Cell t_cell = a_row.createCell(0);
        t_cell.setCellValue("File");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // glycan in file
        t_cell = a_row.createCell(1);
        t_cell.setCellValue("Glycan Number");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GlyTouCan ID
        t_cell = a_row.createCell(2);
        t_cell.setCellValue("GlyTouCan ID");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // Image
        t_cell = a_row.createCell(3);
        t_cell.setCellValue("Image (GWS)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        t_cell = a_row.createCell(4);
        t_cell.setCellValue("Image (GlycoCT)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        t_cell = a_row.createCell(5);
        t_cell.setCellValue("Image (WURCS)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        t_cell = a_row.createCell(6);
        t_cell.setCellValue("Image (WURCS)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        t_cell = a_row.createCell(7);
        t_cell.setCellValue("Image (GlycoCT)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        t_cell = a_row.createCell(8);
        t_cell.setCellValue("Image (GWS)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
    }

    private void writeHeadlineDebug(Row a_row)
    {
        // file
        Cell t_cell = a_row.createCell(0);
        t_cell.setCellValue("File");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // glycan in file
        t_cell = a_row.createCell(1);
        t_cell.setCellValue("Glycan Number");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GlyTouCan ID
        t_cell = a_row.createCell(2);
        t_cell.setCellValue("GlyTouCan ID");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // Failed
        t_cell = a_row.createCell(3);
        t_cell.setCellValue("Failed");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // Error message
        t_cell = a_row.createCell(4);
        t_cell.setCellValue("Error");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // Error info
        t_cell = a_row.createCell(5);
        t_cell.setCellValue("Error info");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // warning
        t_cell = a_row.createCell(6);
        t_cell.setCellValue("Warning");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GWS (Original)
        t_cell = a_row.createCell(7);
        t_cell.setCellValue("GWS (Original)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GlycoCT
        t_cell = a_row.createCell(8);
        t_cell.setCellValue("GlycoCT (from Original GWS)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GlycoCT recoded
        t_cell = a_row.createCell(9);
        t_cell.setCellValue("GlycoCT (recoded after GWS translation)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // WURCS
        t_cell = a_row.createCell(10);
        t_cell.setCellValue("WURCS (from GlycoCT");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // WURCS
        t_cell = a_row.createCell(11);
        t_cell.setCellValue("WURCS (GlyTouCan");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GlycoCT
        t_cell = a_row.createCell(12);
        t_cell.setCellValue("GlycoCT (from WURCS)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GWS
        t_cell = a_row.createCell(13);
        t_cell.setCellValue("GWS (from GlycoCT)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GWS ordered
        t_cell = a_row.createCell(14);
        t_cell.setCellValue("GWS - ordered)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
        // GWS ordered
        t_cell = a_row.createCell(15);
        t_cell.setCellValue("GWS (after registration) - ordered)");
        t_cell.setCellStyle(this.m_cellStyleHeadline);
    }
}
