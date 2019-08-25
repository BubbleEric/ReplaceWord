package win.aspring.replaceword;

import java.awt.EventQueue;
import java.awt.Toolkit;

import win.aspring.replaceword.ui.ReplaceFileText;

/**
 * 描述：入口
 *
 * @author Abel
 * @version 1.0
 * @date 2019/8/24 18:06
 */
public class Index {


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ReplaceFileText frame = new ReplaceFileText();
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                int x = (int) (toolkit.getScreenSize().getWidth() - frame.getWidth()) / 2;
                int y = (int) (toolkit.getScreenSize().getHeight() - frame.getHeight()) / 2;
                //设置位置居中
                frame.setLocation(x, y);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
