package win.aspring.replaceword.utils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import win.aspring.replaceword.Constants;

/**
 * 描述：文件帮助类
 *
 * @author Abel
 * @version 1.0
 * @date 2019/8/24 17:34
 */
public class FileUtil {
    private FileUtil() {
    }

    /**
     * 重命名文件名称
     *
     * @param fileName 旧文件名称
     * @param key      将要替换的内容
     * @param replace  替换为的内容
     * @return 新文件名称
     */
    public static String renameFileName(String fileName, String key, String replace) {
        if (fileName == null || fileName.isEmpty()) {
            throw new NullPointerException("文件名不能为空！");
        }

        if (fileName.contains(key)) {
            fileName = fileName.replaceAll(key, replace);
        }

        return fileName;
    }

    /**
     * 替换文件中所有需要替换的内容 并写入新文件
     *
     * @param oldFile 旧的文件
     * @param newFile 新的文件
     * @param key     将要替换的内容
     * @param replace 替换为的内容
     */
    public static boolean replaceWord(File oldFile, String newFile, String key, String replace) {
        if (newFile == null) {
            return false;
        }
        if (key == null || key.isEmpty() || replace == null || replace.isEmpty()) {
            return false;
        }
        try {
            if (oldFile.getAbsolutePath().endsWith(Constants.SUFFIX_FILE_DOC)) {
                replaceDOC(oldFile, newFile, key, replace);
            } else if (oldFile.getAbsolutePath().endsWith(Constants.SUFFIX_FILE_DOCX)) {
                replaceDOCX(oldFile.getAbsolutePath(), newFile, key, replace);
            } else if (oldFile.getAbsolutePath().startsWith("~")) {
                return true;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 替换文件中所有需要替换的内容
     *
     * @param oldFile 旧的文件
     * @param newFile 新的文件
     * @param key     将要替换的内容
     * @param replace 替换为的内容
     */
    private static void replaceDOC(File oldFile, String newFile, String key, String replace) throws IOException {
        FileInputStream in = new FileInputStream(oldFile);
        HWPFDocument hdt = new HWPFDocument(in);
        //读取word文本内容
        Range range = hdt.getRange();
        //替换文本内容
        range.replaceText(key, replace);

        FileOutputStream out = new FileOutputStream(newFile);
        hdt.write(out);

        in.close();
        hdt.close();
        out.close();
    }

    /**
     * 替换文件中所有需要替换的内容
     *
     * @param oldFile 旧的文件
     * @param newFile 新的文件
     * @param key     将要替换的内容
     * @param replace 替换为的内容
     */
    private static void replaceDOCX(String oldFile, String newFile, String key, String replace) throws IOException {
        XWPFDocument doc = new XWPFDocument(POIXMLDocument.openPackage(oldFile));
        Map<String, String> map = new HashMap<>();
        map.put(key, replace);

        replaceInAllParagraphs(doc.getParagraphs(), map);

        replaceInTables(doc.getTables(), map);

        FileOutputStream out = new FileOutputStream(newFile, true);
        doc.write(out);

        doc.close();
        out.flush();
        out.close();
    }

    /**
     * 从文件中获取字符串
     *
     * @param file 文件
     * @return 返回字符串
     * @throws IOException IO异常
     */
    public static StringBuilder getStringFromFile(File file) throws IOException {
        // 创建字符串构建器
        StringBuilder sb = new StringBuilder();
        // 生成添加编码的bufferedReader，防止中文乱码
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\r\n");
        }
        br.close();
        return sb;
    }

    /**
     * 替换所有段落中的标记
     *
     * @param xwpfParagraphList 段落列表
     * @param params            替换数据
     */
    private static void replaceInAllParagraphs(List<XWPFParagraph> xwpfParagraphList, Map<String, String> params) {
        for (XWPFParagraph paragraph : xwpfParagraphList) {
            if (paragraph.getText() == null || paragraph.getText().equals("")) continue;
            for (String key : params.keySet()) {
                if (paragraph.getText().contains(key)) {
                    replaceInParagraph(paragraph, key, params.get(key));
                }
            }
        }
    }

    /**
     * 替换段落中的字符串
     *
     * @param xwpfParagraph 段落
     * @param oldString     旧数据
     * @param newString     新数据
     */
    private static void replaceInParagraph(XWPFParagraph xwpfParagraph, String oldString, String newString) {
        Map<String, Integer> pos_map = findSubRunPosInParagraph(xwpfParagraph, oldString);
        if (pos_map != null) {
            List<XWPFRun> runs = xwpfParagraph.getRuns();
            XWPFRun modelRun = runs.get(pos_map.get("end_pos"));
            XWPFRun xwpfRun = xwpfParagraph.insertNewRun(pos_map.get("end_pos") + 1);
            xwpfRun.setText(newString);
            if (modelRun.getFontSize() != -1) {
                //默认值是五号字体，但五号字体getFontSize()时，返回-1
                xwpfRun.setFontSize(modelRun.getFontSize());
            }
            xwpfRun.setFontFamily(modelRun.getFontFamily());
            xwpfRun.setBold(modelRun.isBold());
            xwpfRun.setColor(modelRun.getColor());
            xwpfRun.setCapitalized(modelRun.isCapitalized());
            xwpfRun.setCharacterSpacing(modelRun.getCharacterSpacing());
            xwpfRun.setDoubleStrikethrough(modelRun.isDoubleStrikeThrough());
            xwpfRun.setEmbossed(modelRun.isEmbossed());
            xwpfRun.setImprinted(modelRun.isImprinted());
            xwpfRun.setTextScale(modelRun.getTextScale());
            xwpfRun.setItalic(modelRun.isItalic());
            xwpfRun.setKerning(modelRun.getKerning());
            xwpfRun.setLang(modelRun.getLang());
            xwpfRun.setShadow(modelRun.isShadowed());
            xwpfRun.setSmallCaps(modelRun.isSmallCaps());
            xwpfRun.setStrikeThrough(modelRun.isStrikeThrough());
            xwpfRun.setSubscript(modelRun.getSubscript());
            for (int i = pos_map.get("end_pos"); i >= pos_map.get("start_pos"); i--) {
                xwpfParagraph.removeRun(i);
            }
        }
    }


    /**
     * 找到段落中子串的起始XWPFRun下标和终止XWPFRun的下标
     *
     * @param xwpfParagraph 段落
     * @param substring     旧string
     * @return 下标
     */
    private static Map<String, Integer> findSubRunPosInParagraph(XWPFParagraph xwpfParagraph, String substring) {
        List<XWPFRun> runs = xwpfParagraph.getRuns();
        int start_pos = 0;
        int end_pos = 0;
        StringBuilder builder;
        for (int i = 0; i < runs.size(); i++) {
            builder = new StringBuilder();
            start_pos = i;
            for (int j = i; j < runs.size(); j++) {
                if (runs.get(j).getText(runs.get(j).getTextPosition()) == null) continue;
                builder.append(runs.get(j).getText(runs.get(j).getTextPosition()));
                if (builder.toString().equals(substring)) {
                    end_pos = j;
                    Map<String, Integer> map = new HashMap<>();
                    map.put("start_pos", start_pos);
                    map.put("end_pos", end_pos);
                    return map;
                }
            }
        }
        return null;
    }

    /**
     * 替换所有的表格
     *
     * @param xwpfTableList 表格列表
     * @param params        替换参数
     */
    private static void replaceInTables(List<XWPFTable> xwpfTableList, Map<String, String> params) {
        for (XWPFTable table : xwpfTableList) {
            replaceInTable(table, params);
        }
    }

    /**
     * 替换一个表格中的所有行
     *
     * @param xwpfTable 表格
     * @param params    替换参数
     */
    private static void replaceInTable(XWPFTable xwpfTable, Map<String, String> params) {
        List<XWPFTableRow> rows = xwpfTable.getRows();
        replaceInRows(rows, params);
    }


    /**
     * 替换表格中的一行
     *
     * @param rows   表格的一行
     * @param params 替换参数
     */
    private static void replaceInRows(List<XWPFTableRow> rows, Map<String, String> params) {
        for (XWPFTableRow row : rows) {
            replaceInCells(row.getTableCells(), params);
        }
    }

    /**
     * 替换一行中所有的单元格
     *
     * @param xwpfTableCellList 表格中单元格列表
     * @param params            替换参数
     */
    private static void replaceInCells(List<XWPFTableCell> xwpfTableCellList, Map<String, String> params) {
        for (XWPFTableCell cell : xwpfTableCellList) {
            replaceInCell(cell, params);
        }
    }

    /**
     * 替换表格中每一行中的每一个单元格中的所有段落
     *
     * @param cell   单元格中的一行
     * @param params 参数
     */
    private static void replaceInCell(XWPFTableCell cell, Map<String, String> params) {
        List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
        replaceInAllParagraphs(cellParagraphs, params);
    }
}
