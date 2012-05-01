package caterpillarssa;
import java.awt.event.*;
import javax.swing.*;

/**
 * класс,реализующий интерфейс ActionListener, для обработки
 * событий,связанных с пунктом меню "Выход"
 */     
public class ExitListener implements ActionListener
{
    public void actionPerformed(ActionEvent e)
    { 
        //вызываем диалоговое окно
        int result = JOptionPane.showConfirmDialog(null, "Вы действительно хотите выйти?", "Сообщение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) 
        {
            System.exit(0);
        }
    }
}

