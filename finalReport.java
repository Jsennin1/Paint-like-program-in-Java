
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
 
// 描画した図形を記録する Figure クラス (継承して利用する)
class Figure {
  protected int x, y, width, height;
  protected Color color;
  public Figure(int x, int y, int w, int h, Color c) {
    this.x = x; this.y = y;  // this.x, this.y はインスタンス変数．
    width = w; height = h;   // ローカル変数で同名の変数がある場合は，this
    color = c;               // を付けると，インスタンス変数を指す．
  }
  public void setSize(int w, int h) {
    width = w; height = h;
  }
  public void setLocation(int x, int y) {
    this.x = x; this.y = y;
  }
  public void reshape(int x1, int y1, int x2, int y2) {
    int newx = Math.min(x1, x2);
    int newy = Math.min(y1, y2);
    int neww = Math.abs(x1 - x2);
    int newh = Math.abs(y1 - y2);
    setLocation(newx, newy);
    setSize(neww, newh);
  }
  public void draw(Graphics g) {}
}
 
class RectangleFigure extends Figure {
  public RectangleFigure(int x, int y, int w, int h, Color c) {
    super(x, y, w, h, c);
    // 引数付きのコンストラクタは継承されないので，コンストラクタを定義．
    // superで親のコンストラクタを呼び出すだけ．
  }
  public void draw(Graphics g) {
    g.setColor(color);
    g.drawRect(x, y, width, height);
  }
}
class CircleFigure extends Figure {
  public CircleFigure(int x, int y, int w, int h, Color c) {
    super(x, y, w, h, c);
    // 引数付きのコンストラクタは継承されないので，コンストラクタを定義．
    // superで親のコンストラクタを呼び出すだけ．
  }
  public void draw(Graphics g) {
    g.setColor(color);
    g.drawOval(x, y, width, height);
  }

}
class LineFigure extends Figure {
  public LineFigure(int x, int y, int w, int h, Color c) {
    super(x, y, w, h, c);
    // 引数付きのコンストラクタは継承されないので，コンストラクタを定義．
    // superで親のコンストラクタを呼び出すだけ．
  }
  public void draw(Graphics g) {
    g.setColor(color);
    g.drawLine(x, y,width,height);
  }
  //line の計算は違うのでreshapeを作る必要がる
  public void reshape(int x1, int y1, int x2, int y2) {
    setSize(x2, y2);
  }
}
////////////////////////////////////////////////
// Model (M)
 
// modelは java.util.Observableを継承する．Viewに監視される．
class DrawModel extends Observable {
  protected ArrayList<Figure> fig;
  protected Figure drawingFigure;
  protected Color currentColor;
  private ShapeUI shape;
  public DrawModel(ShapeUI shapeUI) {
    fig = new ArrayList<Figure>();
    drawingFigure = null;
    shape = shapeUI;
    currentColor = Color.red;
  }
  public ArrayList<Figure> getFigures() {
    return fig;
  }
  public Figure getFigure(int idx) {
    return fig.get(idx);
  }
  public void createFigure(int x, int y) {
    Figure f;
    //選んだshapeをshape classからintとしてとる
    if(shape.getSelectedShape()==0)
    f = new RectangleFigure(x, y, 0, 0, currentColor);
    else if(shape.getSelectedShape()==1)
    f = new CircleFigure(x, y, 0, 0, currentColor);
    else
    f = new LineFigure(x, y, x, y, currentColor);   /*LineFigure(x, y, 0，0, currentColor)だったらlineは一番右上から始める*/
    fig.add(f);
    drawingFigure = f;
    setChanged();
    notifyObservers();
  }
  public void reshapeFigure(int x1, int y1, int x2, int y2) {
    if (drawingFigure != null) {
      drawingFigure.reshape(x1, y1, x2, y2);
      setChanged();
      notifyObservers();
    }
  }
}
//色を設定するUIを作るclass
class RGBInput extends JFrame implements ActionListener {
    private JTextField red, green,blue;
    private JPanel topPanel,smallPanel1,smallPanel2;
   private JButton setColorBut;
   DrawModel m;
   JLabel infoLabel;
   //色を選ぶUIをここで作る
    public RGBInput(DrawModel model,JPanel panel) {
      red = new JTextField("255");
      blue = new JTextField("0");
      green = new JTextField("0");
      m=model;
      topPanel=panel;
      smallPanel1 = new JPanel();
      smallPanel2 = new JPanel();
      smallPanel1.setLayout(new GridLayout(1,7));
      smallPanel2.setLayout(new GridLayout(1,2));
      smallPanel1.add(new JLabel("    red"));
      smallPanel1.add(red);
      smallPanel1.add(new JLabel("    green"));
      smallPanel1.add(green);
      smallPanel1.add(new JLabel("    blue"));
      smallPanel1.add(blue);
      setColorBut = new JButton("set Color");
      smallPanel1.add(setColorBut);
      smallPanel2.add(new JLabel("if input isnt an integer between 0-255 , default will be 0"));
      infoLabel = new JLabel("r: 255 g:0 b:0");
      smallPanel2.add(infoLabel);
      topPanel.add(smallPanel1);
      topPanel.add(smallPanel2);
      
      setColorBut.addActionListener(this);
      this.pack();
    }
    //strは数だったらtrueを戻す
    private  boolean isNumeric(String str){
        return str != null && str.matches("[0-9]+");
    }
    //input fieldの値をRGBcolor codeに変えて、currentColorに入力する
    public void actionPerformed(ActionEvent e) {
        int redInt,greenInt,blueInt;
        redInt = isNumeric(red.getText()) ? Integer.parseInt(red.getText()) : 0;
        greenInt = isNumeric(green.getText()) ? Integer.parseInt(green.getText()) : 0;
        blueInt = isNumeric(blue.getText()) ? Integer.parseInt(blue.getText()) : 0;
        redInt= redInt >255 ? 0:redInt;
        greenInt= greenInt >255 ? 0:greenInt;
        blueInt= blueInt >255 ? 0:blueInt;
        red.setText(""+ redInt);
        green.setText(""+ greenInt);
        blue.setText(""+ blueInt);
        infoLabel.setText("red "+ redInt + " green "+greenInt+" blue "+ blueInt);



        m.currentColor = new Color( redInt,greenInt,blueInt);
     
    }
  }
//形を設定するUIを作るclass
class ShapeUI extends JFrame implements ActionListener {
    private int selectedShape;
    private JPanel topPanel;
   private JButton rectButton,circleButton, lineButton;
   //形を選ぶUIをここで作る
    public ShapeUI(JPanel panel) {
      topPanel=panel;
      JPanel smallPanel1 = new JPanel();
      smallPanel1.setLayout(new GridLayout(1,3));
      rectButton = new JButton("Rectangle");
      circleButton = new JButton("Circle");
      lineButton = new JButton("Line");
      smallPanel1.add(rectButton);
      smallPanel1.add(circleButton);
      smallPanel1.add(lineButton);
      rectButton.addActionListener(this);
      circleButton.addActionListener(this);
      lineButton.addActionListener(this);
      topPanel.add(smallPanel1);
      this.pack();
    }
    //選ばれた形のintを戻す関数
    public int getSelectedShape(){
      return selectedShape;
    }
    //形のボタンに押すと呼ばれる関数
    public void actionPerformed(ActionEvent e) {
      //e.getSourceからどのボタンに押されたのかを分かる
      if(e.getSource() == rectButton)
      selectedShape = 0;
      else if(e.getSource() == circleButton)
      selectedShape = 1;
      else
      selectedShape = 2;
    }
  }
 
////////////////////////////////////////////////
// View (V)
 
// Viewは，Observerをimplementsする．Modelを監視して，
// モデルが更新されたupdateする．実際には，Modelから
// update が呼び出される．
class ViewPanel extends JPanel implements Observer {
  protected DrawModel model;
  public ViewPanel(DrawModel m, DrawController c) {
    this.setBackground(Color.black);
    this.addMouseListener(c);
    this.addMouseMotionListener(c);
    model = m;
    model.addObserver(this);
  }
  //ArrayList<Figure> figの全部のdraw関数を読んで、画面に描かせる関数
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    ArrayList<Figure> fig = model.getFigures();
    for(int i = 0; i < fig.size(); i++) {
      Figure f = fig.get(i);
      f.draw(g);
    }
  }
  public void update(Observable o, Object arg) {
  //repaintは画面の形をけしてpaintComponentを呼ぶ関数
    repaint();
  }
}
 
//////////////////////////////////////////////////
// Main class
//   (GUIを組み立てているので，view の一部と考えてもよい)
class DrawFrame extends JFrame {
  DrawModel model;
  ViewPanel view;
  DrawController cont;
  RGBInput rgbInput;
  ShapeUI shapeUI;
  public DrawFrame() {
      JPanel topPanel = new JPanel();
      this.add(topPanel,BorderLayout.NORTH);
      topPanel.setLayout(new GridLayout(3,1));
      shapeUI = new ShapeUI(topPanel);
      model = new DrawModel(shapeUI);
      rgbInput = new RGBInput(model,topPanel);
    cont = new DrawController(model);
    view = new ViewPanel(model,cont);
    this.setBackground(Color.black);
    this.setTitle("Draw Editor");
    this.setSize(500, 500);
    this.add(view);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }
  public static void main(String[] args) {
    new DrawFrame();
  }
}
 
////////////////////////////////////////////////
// Controller (C)
 
class DrawController implements MouseListener, MouseMotionListener {
  protected DrawModel model;
  protected int dragStartX, dragStartY;
  public DrawController(DrawModel a) {
    model = a;
  }
  public void mouseClicked(MouseEvent e) {}
  //マウスに押されたら、動く関数
  public void mousePressed(MouseEvent e) {
    dragStartX = e.getX(); dragStartY = e.getY();
    model.createFigure(dragStartX, dragStartY);
  }
  //マウスにdragするとき、動く関数
  public void mouseDragged(MouseEvent e) {
    model.reshapeFigure(dragStartX, dragStartY, e.getX(), e.getY());
  }
  public void mouseReleased(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mouseMoved(MouseEvent e) {}
}