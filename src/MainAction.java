import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

/**
 * @author eleven
 * @date 2021/7/30 12:09
 * @apiNote
 */
public class MainAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 弹窗显示消息
        Messages.showMessageDialog("Hello world", "GG Say", Messages.getInformationIcon());
    }
}
