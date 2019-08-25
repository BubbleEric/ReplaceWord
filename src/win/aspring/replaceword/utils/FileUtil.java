package win.aspring.replaceword.utils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
                replaceDOCX(oldFile, newFile, key, replace);
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

        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        FileOutputStream out = new FileOutputStream(newFile, true);
        hdt.write(ostream);
        //输出字节流
        out.write(ostream.toByteArray());

        in.close();
        hdt.close();
        out.close();
        ostream.close();
    }

    /**
     * 替换文件中所有需要替换的内容
     *
     * @param oldFile 旧的文件
     * @param newFile 新的文件
     * @param key     将要替换的内容
     * @param replace 替换为的内容
     */
    private static void replaceDOCX(File oldFile, String newFile, String key, String replace) throws IOException {
        FileInputStream in = new FileInputStream(oldFile);
        XWPFDocument doc = new XWPFDocument(in);
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(key)) {
                        text = text.replace(key, replace);
                        r.setText(text, 0);
                    }
                }
            }
        }
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(key)) {
                                text = text.replace(key, replace);
                                r.setText(text, 0);
                            }
                        }
                    }
                }
            }
        }
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        FileOutputStream out = new FileOutputStream(newFile, true);
        doc.write(ostream);
        //输出字节流
        out.write(ostream.toByteArray());

        in.close();
        doc.close();
        out.close();
        ostream.close();
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
}
