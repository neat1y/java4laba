package goldirevlaba4;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
public class MainFrame  extends JFrame{
    private static final int WIDTH = 800;
    private boolean value = false;
    private static final int HEIGHT = 600;
    // Объект диалогового окна для выбора файлов
    private JFileChooser fileChooser = null;
    // Пункты меню
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem y90;
    private JButton uslovie;
    private JCheckBoxMenuItem showMarkersMenuItem;
    // Компонент-отображатель графика
    private GraphicsDisplay display = new GraphicsDisplay();
    // Флаг, указывающий на загруженность данных графика
    private boolean fileLoaded = false;

    public MainFrame() {
// Вызов конструктора предка Frame
        super("Построение графиков функций на основе заранее подготовленных файлов");
// Установка размеров окна
                setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
// Отцентрировать окно приложения на экране
        setLocation((kit.getScreenSize().width - WIDTH)/2,
                (kit.getScreenSize().height - HEIGHT)/2);
// Развѐртывание окна на весь экран
        setExtendedState(MAXIMIZED_BOTH);
// Создать и установить полосу меню
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
// Добавить пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
// Создать действие по открытию файла
        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком") {
        public void actionPerformed(ActionEvent event) {
            if (fileChooser==null) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
            }
            if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                openGraphics(fileChooser.getSelectedFile());
        }
    };
// Добавить соответствующий элемент меню
fileMenu.add(openGraphicsAction);
    // Создать пункт меню "График"
    JMenu graphicsMenu = new JMenu("График");
menuBar.add(graphicsMenu);
    // Создать действие для реакции на активацию элемента "Показывать

        Action uslovie1 = new AbstractAction("условие ") {
            public void actionPerformed(ActionEvent event) {
// свойство showAxis класса GraphicsDisplay истина,
              openGraphicsforcondition(fileChooser.getSelectedFile());
// showAxisMenuItem отмечен флажком, и ложь - в

            }
        };
    Action showAxisAction = new AbstractAction("Показывать оси координат") {
    public void actionPerformed(ActionEvent event) {
// свойство showAxis класса GraphicsDisplay истина,

// showAxisMenuItem отмечен флажком, и ложь - в
                display.setShowAxis(showAxisMenuItem.isSelected());
    }
};
        Action x90 = new AbstractAction("поворот") {
            public void actionPerformed(ActionEvent event) {
// свойство showAxis класса GraphicsDisplay истина,
                    if(!value) {
                        openGraphics1(fileChooser.getSelectedFile());
                        value = true;
                    }
                else{
                        value = false;
                        openGraphics(fileChooser.getSelectedFile());
                    }
// showAxisMenuItem отмечен флажком, и ложь - в
             display.setShowAxis(true);
            }
        };
        y90=new JCheckBoxMenuItem(x90);
showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
uslovie=new JButton(uslovie1);
// Добавить соответствующий элемент в меню
        graphicsMenu.add(showAxisMenuItem);
        graphicsMenu.add(uslovie);
        graphicsMenu.add(y90);
// Элемент по умолчанию включен (отмечен флажком)
        showAxisMenuItem.setSelected(true);
// Повторить действия для элемента "Показывать маркеры точек"
        Action showMarkersAction = new AbstractAction("Показывать маркеры точек") {
public void actionPerformed(ActionEvent event) {
// по аналогии с showAxisMenuItem
        display.setShowMarkers(showMarkersMenuItem.isSelected());
        }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
// Элемент по умолчанию включен (отмечен флажком)
        showMarkersMenuItem.setSelected(true);
// Зарегистрировать обработчик событий, связанных с меню "График"
        graphicsMenu.addMenuListener(new GraphicsMenuListener());
// Установить GraphicsDisplay в цент граничной компоновки
        getContentPane().add(display, BorderLayout.CENTER);
        }
        protected void openGraphicsforcondition(File selectedFile) {
        try {
// Шаг 1 - Открыть поток чтения данных, связанный с входным

            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
/* Шаг 2 - Зная объм данных в потоке ввода можно вычислить,
* сколько памяти нужно зарезервировать в массиве:
* Всего байт в потоке - in.available() байт;
* Размер одного числа Double - Double.SIZE бит, или
Double.SIZE/8 байт;
* Так как числа записываются парами, то число пар меньше в
2 раза
*/
            Double[][] graphicsData = new
                    Double[in.available() / (Double.SIZE / 8) - 1][];
// Шаг 3 - Цикл чтения данных (пока в потоке есть данные)
            String k;
            int i = 0;
            while (in.available() > 0) {
                k = in.readLine();
                System.out.println(k);
                String[] words = k.split(" ");

                Double x = Double.parseDouble(words[0]);
                Double y = Double.parseDouble(words[1]);

                graphicsData[i++] = new Double[]{x, y};

            }
            boolean isSorted = false;
            double buf1,buf2;
            while(!isSorted) {
                isSorted = true;
                for (i = 0; i < graphicsData.length-1; i++) {
                    if(graphicsData[i][1] > graphicsData[i+1][1]){
                        isSorted = false;

                        buf1 = graphicsData[i][0];
                        buf2=graphicsData[i][1];
                        graphicsData[i] = graphicsData[i+1];
                        graphicsData[i+1][0] = buf1;
                        graphicsData[i+1][1] = buf2;
                    }
                }
            }
           for( i=0;i<graphicsData.length;i++){
               System.out.println(graphicsData[i][0]+" "+graphicsData[i][1]);
           }
        }catch (FileNotFoundException ex) {
// В случае исключительной ситуации типа "Файл не найден"

            JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return ;
        } catch (IOException ex) {
// В случае ошибки ввода из файлового потока показать

            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

    }
    protected void openGraphics1(File selectedFile) {
        try {
// Шаг 1 - Открыть поток чтения данных, связанный с входным

            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
/* Шаг 2 - Зная объм данных в потоке ввода можно вычислить,
* сколько памяти нужно зарезервировать в массиве:
* Всего байт в потоке - in.available() байт;
* Размер одного числа Double - Double.SIZE бит, или
Double.SIZE/8 байт;
* Так как числа записываются парами, то число пар меньше в
2 раза
*/
            Double[][] graphicsData = new Double[20][];
// Шаг 3 - Цикл чтения данных (пока в потоке есть данные)
            String k;
            int i=0;
            while(in.available()>0) {
                k=in.readLine();

                String[] words = k.split(" ");

                Double y=Double.parseDouble(words[0]);
                Double x=-Double.parseDouble(words[1]);

                graphicsData[i++] = new Double[] {x, y};

            }

// Шаг 4 - Проверка, имеется ли в списке в результате чтения

            if (graphicsData!=null && graphicsData.length>0) {
// Да - установить флаг загруженности данных
                fileLoaded = true;
// Вызывать метод отображения графика
                display.showGraphics(graphicsData);
            }
// Шаг 5 - Закрыть входной поток
            in.close();
        } catch (FileNotFoundException ex) {
// В случае исключительной ситуации типа "Файл не найден"

            JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (IOException ex) {
// В случае ошибки ввода из файлового потока показать

            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
    }
protected void openGraphics(File selectedFile) {
        try {
// Шаг 1 - Открыть поток чтения данных, связанный с входным

        DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
/* Шаг 2 - Зная объм данных в потоке ввода можно вычислить,
* сколько памяти нужно зарезервировать в массиве:
* Всего байт в потоке - in.available() байт;
* Размер одного числа Double - Double.SIZE бит, или
Double.SIZE/8 байт;
* Так как числа записываются парами, то число пар меньше в
2 раза
*/
            Double[][] graphicsData = new
                    Double[20][];
// Шаг 3 - Цикл чтения данных (пока в потоке есть данные)
            String k;
            int i=0;
            while(in.available()>0) {
                k=in.readLine();

                String[] words = k.split(" ");

                Double x=Double.parseDouble(words[0]);
                Double y=Double.parseDouble(words[1]);

                graphicsData[i++] = new Double[] {x, y};

            }

// Шаг 4 - Проверка, имеется ли в списке в результате чтения

        if (graphicsData!=null && graphicsData.length>0) {
// Да - установить флаг загруженности данных
        fileLoaded = true;
// Вызывать метод отображения графика
        display.showGraphics(graphicsData);
        }
// Шаг 5 - Закрыть входной поток
        in.close();
        } catch (FileNotFoundException ex) {
// В случае исключительной ситуации типа "Файл не найден"

        JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
        return;
        } catch (IOException ex) {
// В случае ошибки ввода из файлового потока показать

        JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных",
        JOptionPane.WARNING_MESSAGE);
        return;
        }
        }
public static void main(String[] args) {
// Создать и показать экземпляр главного окна приложения
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        }
// Класс-слушатель событий, связанных с отображением меню
private class GraphicsMenuListener implements MenuListener {
    // Обработчик, вызываемый перед показом меню
    public void menuSelected(MenuEvent e) {
// Доступность или недоступность элементов меню "График"

        showAxisMenuItem.setEnabled(fileLoaded);
        showMarkersMenuItem.setEnabled(fileLoaded);
    }
    // Обработчик, вызываемый после того, как меню исчезло с экрана
    public void menuDeselected(MenuEvent e) {
    }
// Обработчик, вызываемый в случае отмены выбора пункта меню

    public void menuCanceled(MenuEvent e) {
    }
}
}


