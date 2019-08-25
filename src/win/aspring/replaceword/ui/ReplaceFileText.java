package win.aspring.replaceword.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import win.aspring.replaceword.Constants;
import win.aspring.replaceword.utils.FileUtil;

/**
 * 描述：界面
 *
 * @author Abel
 * @version 1.0
 * @date 2019/8/24 18:30
 */
public class ReplaceFileText extends JFrame {

    private static final long serialVersionUID = 8674569541853793419L;
    private static final String REPLACE_WORD = "替换：";

    /**
     * 模板路径框
     */
    private JTextField mTemplateDirField;
    /**
     * 模板路径文件
     */
    private File mCurrentTemplateFile;
    /**
     * 替换文件框
     */
    private JTextField mSearchTextField;
    /**
     * 将要替换的内容
     */
    private String mSearchText;
    /**
     * 要替换的内容列表
     */
    private List<String> mTargetTexts = new ArrayList<>();
    /**
     * 输出目录框
     */
    private JTextField mOutDirField;
    /**
     * 输出目录文件
     */
    private File mCurrentOutFile;

    /**
     * Create the frame.
     */
    public ReplaceFileText() {
        setTitle("替换工具");
        setResizable(true);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 300);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 10, 15, 10));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        GridBagLayout bagLayout = new GridBagLayout();
        bagLayout.columnWidths = new int[]{120, 0, 0, 90, 0};
        bagLayout.rowHeights = new int[]{4, 0, 0, 0, 0};
        bagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0,
                Double.MIN_VALUE};
        bagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(bagLayout);

        Font font = new Font("楷体", Font.PLAIN, 18);

        GridBagConstraints constraints = new GridBagConstraints();
        //添加选择模板目录按钮
        JButton jSelectDir = new JButton("选择模板目录");
        jSelectDir.setFont(font);
        jSelectDir.addActionListener(e -> doSelectDir(1, "请选择模板目录"));
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 0, 5, 5);
        constraints.gridx = 0;
        constraints.gridy = 0;
        contentPane.add(jSelectDir, constraints);

        //添加模板目录文本框
        mTemplateDirField = new JTextField();
        mTemplateDirField.setEditable(false);
        mTemplateDirField.setFont(font);
        constraints.gridwidth = 3;
        constraints.insets = new Insets(5, 0, 5, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        contentPane.add(mTemplateDirField, constraints);
        mTemplateDirField.setColumns(10);

        //添加选择替换文件按钮
        JButton jReplaceButton = new JButton("选择替换文件");
        jReplaceButton.setFont(font);
        jReplaceButton.addActionListener(e -> doSelectFile());
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 0, 5, 5);
        constraints.gridx = 0;
        constraints.gridy = 1;
        contentPane.add(jReplaceButton, constraints);

        //添加替换文件路径文本框
        mSearchTextField = new JTextField();
        mSearchTextField.setEditable(false);
        mSearchTextField.setFont(font);
        constraints.gridwidth = 3;
        constraints.insets = new Insets(5, 0, 5, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 1;
        contentPane.add(mSearchTextField, constraints);
        mSearchTextField.setColumns(10);

        //添加选择输出目录按钮
        JButton jOutDir = new JButton("选择输出目录");
        jOutDir.setFont(font);
        jOutDir.addActionListener(e -> doSelectDir(2, "请选择输出目录"));
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 0, 5, 5);
        constraints.gridx = 0;
        constraints.gridy = 2;
        contentPane.add(jOutDir, constraints);

        //添加输出目录文本框
        mOutDirField = new JTextField();
        mOutDirField.setFont(font);
        mOutDirField.setEditable(false);
        constraints.gridwidth = 3;
        constraints.insets = new Insets(5, 0, 5, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 2;
        contentPane.add(mOutDirField, constraints);
        mOutDirField.setColumns(10);

        //添加开始替换按钮
        JButton replaceButton = new JButton("开始替换");
        replaceButton.setFont(font);
        replaceButton.addActionListener(e -> doActionReplace());
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(5, 0, 10, 0);
        constraints.fill = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = 3;
        contentPane.add(replaceButton, constraints);
    }

    /**
     * 选择目录
     *
     * @param type  类型 1，模板路径，2，输出目录
     * @param title 显示标题
     */
    private void doSelectDir(int type, String title) {
        if (title == null || title.isEmpty()) {
            title = "请选择目录";
        }
        // 创建文件选择器
        JFileChooser chooser = new JFileChooser();
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        chooser.setCurrentDirectory(fileSystemView.getHomeDirectory());
        chooser.setDialogTitle(title);
        // 设置文件选择模式
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setApproveButtonText("确定");
        // 显示文件打开对话框
        int option = chooser.showOpenDialog(this);
        // 确定用户按下打开按钮，而非取消按钮
        if (option != JFileChooser.APPROVE_OPTION)
            return;
        if (type == 1) {
            // 获取用户选择的文件对象
            mCurrentTemplateFile = chooser.getSelectedFile();
            // 显示文件信息到文本框
            mTemplateDirField.setText(mCurrentTemplateFile.getAbsolutePath());
        } else if (type == 2) {
            // 获取用户选择的文件对象
            mCurrentOutFile = chooser.getSelectedFile();
            // 显示文件信息到文本框
            mOutDirField.setText(mCurrentOutFile.getAbsolutePath());
        }
    }

    /**
     * 选择文件 仅支持txt文件
     */
    private void doSelectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("仅能打开 *.txt", "txt"));
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        chooser.setCurrentDirectory(fileSystemView.getHomeDirectory());
        chooser.setDialogTitle("请选择文件");
        // 设置文件选择模式
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setApproveButtonText("确定");
        // 显示文件打开对话框
        int option = chooser.showOpenDialog(this);
        // 确定用户按下打开按钮，而非取消按钮
        if (option != JFileChooser.APPROVE_OPTION)
            return;

        // 获取用户选择的文件对象
        File file = chooser.getSelectedFile();
        // 显示文件信息到文本框
        mSearchTextField.setText(file.getAbsolutePath());
        getTargetText(file.getAbsoluteFile());
    }

    /**
     * 获取将要替换的内容和替换的内容的列表
     *
     * @param file 目标文件
     */
    private void getTargetText(File file) {
        try {
            // 创建文件输入流
            String text = FileUtil.getStringFromFile(file).toString();
            String[] arr = text.split("\r\n");
            //是否是“替换开头”
            if (!arr[0].startsWith(REPLACE_WORD)) {
                JOptionPane.showMessageDialog(null, file.getName() + "文件格式有误！");
                return;
            }
            //截取将要替换的内容
            mSearchText = arr[0].substring(REPLACE_WORD.length());
            //去除第一项，生成新的数据
            String[] temp = new String[arr.length - 1];
            System.arraycopy(arr, 1, temp, 0, temp.length);
            // 把数组转为list
            mTargetTexts = Arrays.asList(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 替换按钮的事件处理方法
     */
    private void doActionReplace() {
        if (mCurrentTemplateFile == null) {
            JOptionPane.showMessageDialog(null, "请选择模板路径！");
            return;
        }
        if (mSearchText == null || mSearchText.isEmpty() || mTargetTexts.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请选择替换配置文件！");
            return;
        }
        //如果输出目录为空，赋值为桌面
        if (mCurrentOutFile == null) {
            mCurrentOutFile = FileSystemView.getFileSystemView().getHomeDirectory();
        }

        //获取所有模板文件
        File[] fileArr = mCurrentTemplateFile.listFiles();
        if (fileArr == null || fileArr.length == 0) {
            JOptionPane.showMessageDialog(null, "模板路径下没有文件！");
            return;
        }

        boolean isTrue = false;

        //根据替换内容列表轮询
        for (String target : mTargetTexts) {
            //在输出目录生成新的目录 “/项目/target/”
            File newDir = new File(mCurrentOutFile, Constants.PROJECT_DIR + File.separator + target);
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            //根据模板文件轮询
            for (File file : fileArr) {
                String oldName = file.getName();
                //替换旧名字中文字，生成新的文件名
                String newName = FileUtil.renameFileName(oldName, mSearchText, target);
                //拼接新的路径和文件名
                String newPath = newDir.getAbsolutePath() + File.separator + newName;
                //替换文件中内容，并写入新文件
                isTrue = FileUtil.replaceWord(file, newPath, mSearchText, target);
                if (!isTrue) {
                    break;
                }
            }
            if (!isTrue) {
                break;
            }
        }

        String message = "替换失败";
        if (isTrue) {
            message = "替换成功";
        }
        JOptionPane.showMessageDialog(null, message);
    }
}
