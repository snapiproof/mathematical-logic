public class MyTree {
    MyTree left;
    MyTree right;
    MyTree parent;
    String value;
    boolean otr = false;

    boolean number;

    public void setNumber(boolean number) {
        this.number = number;
    }

    public boolean getNumber() {
        return number;
    }

    public void setOtr(boolean otr) {
        this.otr = otr;
    }


    public MyTree(int value){
        value = value;
    }

    public void setLeft(MyTree left) {
        this.left = left;
    }

    public void setParent(MyTree parent) {
        this.parent = parent;
    }

    public void setRight(MyTree right) {
        this.right = right;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean getOtr() {
        return otr;
    }

    public MyTree getLeft() {
        return left;
    }

    public MyTree getParent() {
        return parent;
    }

    public MyTree getRight() {
        return right;
    }
}
