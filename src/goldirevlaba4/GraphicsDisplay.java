package goldirevlaba4;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import static java.lang.Math.sqrt;
import static javax.swing.text.StyleConstants.setBackground;

public class GraphicsDisplay extends JPanel{
    private boolean valuegraphick=false;
    private Double[][] graphicsData;
    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis = true;
    private boolean value = false;
    private boolean showMarkers = true;
    // Границы диапазона пространства, подлежащего отображению
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    // Используемый масштаб отображения
    private double scale;
    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    // Различные шрифты отображения надписей
    private Font axisFont;
    public GraphicsDisplay() {
// Цвет заднего фона области отображения - белый
        setBackground(Color.white);
// Сконструировать необходимые объекты, используемые в рисовании
// Перо для рисования графика
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
// Перо для рисования осей координат
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Перо для рисования контуров маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Шрифт для подписей осей координат
        axisFont = new Font("Serif", Font.BOLD, 36);
    }
    public void showGraphics(Double[][] graphicsData) {
// Сохранить массив точек во внутреннем поле класса


         this.graphicsData=graphicsData;

        repaint();
    }


    // Методы-модификаторы для изменения параметров отображения графика
// Изменение любого параметра приводит к перерисовке области
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }
    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }
    // Метод отображения всего компонента, содержащего график
    public void paintComponent(Graphics g) {
        /* Шаг 1 - Вызвать метод предка для заливки области цветом заднего фона
         * Эта функциональность - единственное, что осталось в наследство от
         * paintComponent класса JPanel
         */
        super.paintComponent(g);
// Шаг 2 - Если данные графика не загружены (при показе компонента

        if (graphicsData==null || graphicsData.length==0) return;
// Шаг 3 - Определить минимальное и максимальное значения для

// Это необходимо для определения области пространства, подлежащей

// Еѐ верхний левый угол это (minX, maxY) - правый нижний это
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;
// Найти минимальное и максимальное значение функции
        for (int i = 1; i<graphicsData.length; i++) {
            if (graphicsData[i][1]<minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY) {
                maxY = graphicsData[i][1];
            }
        }
/* Шаг 4 - Определить (исходя из размеров окна) масштабы по осям X
и Y - сколько пикселов
* приходится на единицу длины по X и по Y
*/
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
// Шаг 5 - Чтобы изображение было неискажѐнным - масштаб должен

// Выбираем за основу минимальный
        scale = Math.min(scaleX, scaleY);
// Шаг 6 - корректировка границ отображаемой области согласно

        if (scale==scaleX) {
/* Если за основу был взят масштаб по оси X, значит по оси Y
делений меньше,
* т.е. подлежащий визуализации диапазон по Y будет меньше
высоты окна.
* Значит необходимо добавить делений, сделаем это так:
* 1) Вычислим, сколько делений влезет по Y при выбранном
масштабе - getSize().getHeight()/scale
* 2) Вычтем из этого сколько делений требовалось изначально
* 3) Набросим по половине недостающего расстояния на maxY и
minY
*/
            double yIncrement = (getSize().getHeight()/scale - (maxY - minY))/2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale==scaleY) {
// Если за основу был взят масштаб по оси Y, действовать по

            double xIncrement = (getSize().getWidth()/scale - (maxX - minX))/2;
            maxX += xIncrement;
            minX -= xIncrement;
        }
// Шаг 7 - Сохранить текущие настройки холста
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
// Шаг 8 - В нужном порядке вызвать методы отображения элементов

// Порядок вызова методов имеет значение, т.к. предыдущий рисунок

// Первыми (если нужно) отрисовываются оси координат.
        if (showAxis) paintAxis(canvas);
// Затем отображается сам график
        paintGraphics(canvas);
// Затем (если нужно) отображаются маркеры точек, по которым

        if (showMarkers) paintMarkers(canvas);
// Шаг 9 - Восстановить старые настройки холста
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }
    // Отрисовка графика по прочитанным координатам

    protected void paintGraphics(Graphics2D canvas) {
// Выбрать линию для рисования графика
        canvas.setStroke(graphicsStroke);
// Выбрать цвет линии

/* Будем рисовать линию графика как путь, состоящий из множества
сегментов (GeneralPath)
* Начало пути устанавливается в первую точку графика, после чего
прямой соединяется со
* следующими точками
*/      canvas.setColor(Color.black);
        GeneralPath graphics = new GeneralPath();
        BasicStroke myStroke2 = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, new float[] {4, 2, 1, 2}, 0.0f);
        canvas.setStroke(myStroke2);
        for (int i=0; i<graphicsData.length; i++) {

            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
// Преобразовать значения (x,y) в точку на экране point
            if(i==0){

                graphics.moveTo(point.getX(), point.getY());


            }
            graphics.lineTo(point.getX(), point.getY());
        }
// Отобразить график
        canvas.draw(graphics);
    }
    // Отображение маркеров точек, по которым рисовался график
    protected void paintDashes(Graphics2D canvas){
        FontRenderContext context = canvas.getFontRenderContext();
        double stepX = (maxX - minX) / 20;
        double stepY = (maxY - minY) / 20;


        for (int i = 0; i < 11; i++) {
            if (i % 10 == 0) {
                //X
                canvas.draw(new Line2D.Double(xyToPoint(stepX * i, -stepX / 2), xyToPoint(stepX * i, stepX / 2)));
                canvas.draw(new Line2D.Double(xyToPoint(-stepX * i, -stepX / 2), xyToPoint(-stepX * i, stepX / 2)));

                //Y
                canvas.draw(new Line2D.Double(xyToPoint(-stepY / 2, stepY * i), xyToPoint(stepY / 2, stepY * i)));
                canvas.draw(new Line2D.Double(xyToPoint(-stepY / 2, -stepY * i), xyToPoint(stepY / 2, -stepY * i)));

            } else if (i % 5 == 0) {
                //X
                canvas.draw(new Line2D.Double(xyToPoint(stepX * i, -stepX / 5), xyToPoint(stepX * i, stepX / 5)));
                canvas.draw(new Line2D.Double(xyToPoint(-stepX * i, -stepX / 5), xyToPoint(-stepX * i, stepX / 5)));

                //Y
                canvas.draw(new Line2D.Double(xyToPoint(-stepY / 5, stepY * i), xyToPoint(stepY / 5, stepY * i)));
                canvas.draw(new Line2D.Double(xyToPoint(-stepY / 5, -stepY * i), xyToPoint(stepY / 5, -stepY * i)));

            } else {
                //X
                canvas.draw(new Line2D.Double(xyToPoint(stepX * i, -stepX / 10), xyToPoint(stepX * i, stepX / 10)));
                canvas.draw(new Line2D.Double(xyToPoint(-stepX * i, -stepX / 10), xyToPoint(-stepX * i, stepX / 10)));

                //Y
                canvas.draw(new Line2D.Double(xyToPoint(-stepY / 10, stepY * i), xyToPoint(stepY / 10, stepY * i)));
                canvas.draw(new Line2D.Double(xyToPoint(-stepY / 10, -stepY * i), xyToPoint(stepY / 10, -stepY * i)));
            }
            //X
            String plus_value_x = String.valueOf(BigDecimal.valueOf(stepX * i).setScale(1, RoundingMode.HALF_UP));
            Rectangle2D bounds_plus_x = axisFont.getStringBounds(plus_value_x, context);
            Point2D.Double labelPlusPos = xyToPoint(stepX * i, -stepX);
            canvas.drawString(plus_value_x, (float) (labelPlusPos.getX() - bounds_plus_x.getWidth() / 2),
                    (float) (labelPlusPos.getY() - bounds_plus_x.getY()));

            //Y
            String plus_value_Y = String.valueOf(BigDecimal.valueOf(stepY * i).setScale(1, RoundingMode.HALF_UP));
            Rectangle2D bounds_plus = axisFont.getStringBounds(plus_value_Y, context);
            Point2D.Double labelPlusPosX = xyToPoint(-stepY, stepY * i);
            canvas.drawString(plus_value_Y, (float) (labelPlusPosX.getX() - bounds_plus.getWidth() / 2),
                    (float) (labelPlusPosX.getY() + bounds_plus.getHeight() / 4));

            if (i != 0) {
                Point2D.Double labelMinusPosX = xyToPoint(-stepX * i, -stepX);
                String minus_value_x = String.valueOf(BigDecimal.valueOf(-stepX * i).setScale(1, RoundingMode.HALF_UP));
                Rectangle2D bounds_minus_x = axisFont.getStringBounds(minus_value_x, context);
                canvas.drawString(minus_value_x, (float) (labelMinusPosX.getX() - bounds_minus_x.getWidth() / 2),
                        (float) (labelMinusPosX.getY() - bounds_minus_x.getY()));

                Point2D.Double labelMinusPosY = xyToPoint(-stepY, -stepY * i);
                String minus_value_y = String.valueOf(BigDecimal.valueOf(-stepY * i).setScale(1, RoundingMode.HALF_UP));
                Rectangle2D bounds_minus_y = axisFont.getStringBounds(minus_value_y, context);
                canvas.drawString(minus_value_y, (float) (labelMinusPosY.getX() - bounds_plus.getWidth() / 2),
                        (float) (labelMinusPosY.getY() + bounds_minus_y.getHeight() / 4));
            }
        }
        canvas.setFont(axisFont);
    }
    protected void paintMarkers(Graphics2D canvas) {
// Шаг 1 - Установить специальное перо для черчения контуров

        canvas.setStroke(markerStroke);
// Выбрать красный цвета для контуров маркеров
        canvas.setColor(Color.green);
// Выбрать красный цвет для закрашивания маркеров внутри
        canvas.setPaint(Color.green);
// Шаг 2 - Организовать цикл по всем точкам графика

        for (Double[] point: graphicsData) {
// Инициализировать эллипс как объект для представления


/* Эллипс будет задаваться посредством указания координат
его центра
и угла прямоугольника, в который он вписан */
// Центр - в точке (x,y)
            GeneralPath.Double mymarker=new GeneralPath.Double();
            Ellipse2D.Double marker = new Ellipse2D.Double();
/* Эллипс будет задаваться посредством указания координат
его центра
и угла прямоугольника, в который он вписан */
// Центр - в точке (x,y)
            Point2D.Double center = xyToPoint(point[0], point[1]);
// Угол прямоугольника - отстоит на расстоянии (3,3)
            Point2D.Double corner = shiftPoint(center, 6, 6);
// Задать эллипс по центру и диагонали
            marker.setFrameFromCenter(center, corner);
            Point2D.Double from = shiftPoint(center, 6, 0);
            Point2D.Double to = shiftPoint(center, 0, 6);
            Line2D.Double line90=new Line2D.Double(from,center);
            Line2D.Double line180=new Line2D.Double(to,center);
            Point2D.Double from1 = shiftPoint(center, -6, 0);
            Point2D.Double to1 = shiftPoint(center, 0, -6);
            Line2D.Double line901=new Line2D.Double(from1,center);
            Line2D.Double line1801=new Line2D.Double(to1,center);
            mymarker.append(line90,true);
            mymarker.append(marker,true);
            mymarker.append(line180,false);
            mymarker.append(line1801,false);
            mymarker.append(line901,true);
            canvas.draw(mymarker); // Начертить контур маркера

           // Залить внутреннюю область маркера
        }
    }


    // Метод, обеспечивающий отображение осей координат
    protected void paintAxis(Graphics2D canvas) {
// Установить особое начертание для осей
        canvas.setStroke(axisStroke);
// Оси рисуются чѐрным цветом
        canvas.setColor(Color.BLACK);
// Стрелки заливаются чѐрным цветом
        canvas.setPaint(Color.BLACK);
// Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
// Создать объект контекста отображения текста - для получения

                FontRenderContext context = canvas.getFontRenderContext();
// Определить, должна ли быть видна ось Y на графике
        if (minX<=0.0 && maxX>=0.0) {
// Она должна быть видна, если левая граница показываемой
// (minX) <= 0.0,
// а правая (maxX) >= 0.0
// Сама ось - это линия между точками (0, maxY) и (0, minY)
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY),
                            xyToPoint(0, minY)));
// Стрелка оси Y
            GeneralPath arrow = new GeneralPath();
// Установить начальную точку ломаной точно на верхний конец
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
// Вести левый "скат" стрелки в точку с относительными
            arrow.lineTo(arrow.getCurrentPoint().getX()+5,
                    arrow.getCurrentPoint().getY()+20);
// Вести нижнюю часть стрелки в точку с относительными
            arrow.lineTo(arrow.getCurrentPoint().getX()-10,
                    arrow.getCurrentPoint().getY());
// Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
// Нарисовать подпись к оси Y
// Определить, сколько места понадобится для надписи "y"
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
// Вывести надпись в точке с вычисленными координатами
            canvas.drawString("y", (float)labelPos.getX() + 10,
                    (float)(labelPos.getY() - bounds.getY()));
        }
// Определить, должна ли быть видна ось X на графике
        if (minY<=0.0 && maxY>=0.0) {
// Она должна быть видна, если верхняя граница показываемой

// а нижняя (minY) <= 0.0
                    canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
                            xyToPoint(maxX, 0)));
// Стрелка оси X
            GeneralPath arrow = new GeneralPath();
// Установить начальную точку ломаной точно на правый конец

            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
// Вести верхний "скат" стрелки в точку с относительными

            arrow.lineTo(arrow.getCurrentPoint().getX()-20,
                    arrow.getCurrentPoint().getY()-5);
// Вести левую часть стрелки в точку с относительными

            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY()+10);
// Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
// Нарисовать подпись к оси X
// Определить, сколько места понадобится для надписи "x"
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
// Вывести надпись в точке с вычисленными координатами
            canvas.drawString("x", (float)(labelPos.getX() -
                    bounds.getWidth() - 10), (float)(labelPos.getY() + bounds.getY()));
        }


    }
    /* Метод-помощник, осуществляющий преобразование координат.
    * Оно необходимо, т.к. верхнему левому углу холста с координатами
    * (0.0, 0.0) соответствует точка графика с координатами (minX, maxY),
    где
    * minX - это самое "левое" значение X, а
    * maxY - самое "верхнее" значение Y.
    */
    protected Point2D.Double xyToPoint(double x, double y) {
// Вычисляем смещение X от самой левой точки (minX)
        double deltaX = x - minX;
// Вычисляем смещение Y от точки верхней точки (maxY)
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }
    /* Метод-помощник, возвращающий экземпляр класса Point2D.Double
     * смещѐнный по отношению к исходному на deltaX, deltaY
     * К сожалению, стандартного метода, выполняющего такую задачу, нет.
     */
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
                                        double deltaY) {
// Инициализировать новый экземпляр точки
        Point2D.Double dest = new Point2D.Double();
// Задать еѐ координаты как координаты существующей точки +
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }

}
