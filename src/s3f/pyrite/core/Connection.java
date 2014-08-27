/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import com.falstad.circuit.elements.WireElm;
import java.util.Objects;

/**
 *
 * @author antunes
 */
public class Connection extends Fixable {

    public Object whut = null;
    private static int ID = 0;
    //id
    private int uid;
    private Component a;
    private Component b;
    private int terminalA;
    private int terminalB;
    private String subComponent;
    private boolean satisfied;

    @Deprecated //adicionar manualmente
    public Connection(String subComponent) {
        uid = ID++;
        this.subComponent = subComponent;
        satisfied = false;
    }

    public Connection(Component a, int terminalA, Component b, int terminalB, String subComponent) {
        this(subComponent);
        this.a = a;
        this.b = b;
        this.terminalA = terminalA;
        this.terminalB = terminalB;
        a.addConnection(this);
        b.addConnection(this);
    }

    public Connection(Component a, Component b, String subComponent) {
        this(a, 0, b, 0, subComponent);
    }

    public Connection(Component a, Component b) {
        this(a, 0, b, 0, "");
    }

    public Component getA() {
        return a;
    }

    public void setA(Component a) {
        this.a = a;
    }

    public Component getB() {
        return b;
    }

    public void setB(Component b) {
        this.b = b;
    }

    public int getTerminalA() {
        return terminalA;
    }

    public void setTerminalA(int terminalA) {
        this.terminalA = terminalA;
    }

    public int getTerminalB() {
        return terminalB;
    }

    public int getTerminal(Component c) {
        if (c == a) {
            return terminalA;
        } else {
            return terminalB;
        }
    }

    public void setTerminalB(int terminalB) {
        this.terminalB = terminalB;
    }

    @Override
    public void setConsumed(boolean consumed) {
        super.setConsumed(consumed);
        if (consumed) {
            a.removeConnection(this);
            b.removeConnection(this);
        }
    }

    public String getSubComponent() {
        return subComponent;
    }

    public void setSubComponent(String subComponent) {
        this.subComponent = subComponent;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

    public boolean isShort() {
        return subComponent.isEmpty() && (whut == null || whut instanceof WireElm);
    }

    @Override
    public String toString() {
        return (isConsumed() ? "!" : "") + a.getUID() + " [" + terminalA + "]" + (subComponent.isEmpty() ? " -> " : " --(" + subComponent + ")-> ") + "[" + terminalB + "] " + b.getUID() + "." + whut + "." + uid;
    }

    public Component getOtherComponent(Component c) {
        if (a == c) {
            return b;
        } else {
            return a;
        }
    }

    public void replace(Component c, Component other) {
        if (a == c) {
            setA(other);
        } else {
            setB(other);
        }
    }

    void setTerminal(Component c, int terminal) {
        if (a == c) {
            setTerminalA(terminal);
        } else {
            setTerminalB(terminal);
        }
    }

    void setOtherTerminal(Component c, int terminal) {
        if (a != c) {
            setTerminalA(terminal);
        } else {
            setTerminalB(terminal);
        }
    }

    void debug(Component c) {
        if (c.getUID().startsWith("10")) {
            System.out.println(c.getConnections().size());
        }
    }

    public Connection shiftA(Component splitter) {
        System.out.println("-");
        Connection con = splitter.getConnection(a);
        if (con == null) {
            con = splitter.createConnection(a);
        }
        System.out.println(splitter + " " + splitter.getConnections().size());
        con.setOtherTerminal(splitter, terminalA);
        terminalA = 0;

        Connection x = b.getConnection(splitter);
        if (x != null && x != this) {
            x.setConsumed(true);
        }
        x = a.getConnection(splitter);
        if (x != null && x != con) {
            x.setConsumed(true);
        }
        a.removeConnection(this);
        a = splitter;
        splitter.addConnection(this);
        return con;
    }

    public void swap() {
        Component t = a;
        a = b;
        b = t;
        int i = terminalA;
        terminalA = terminalB;
        terminalB = i;
    }

//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 37 * hash + Objects.hashCode(this.whut);
//        hash = 37 * hash + Objects.hashCode(this.a);
//        hash = 37 * hash + Objects.hashCode(this.b);
//        hash = 37 * hash + this.terminalA;
//        hash = 37 * hash + this.terminalB;
//        hash = 37 * hash + Objects.hashCode(this.subComponent);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final Connection other = (Connection) obj;
//        if (!Objects.equals(this.whut, other.whut)) {
//            return false;
//        }
//        if (!Objects.equals(this.a, other.a)) {
//            return false;
//        }
//        if (!Objects.equals(this.b, other.b)) {
//            return false;
//        }
//        if (this.terminalA != other.terminalA) {
//            return false;
//        }
//        if (this.terminalB != other.terminalB) {
//            return false;
//        }
//        if (!Objects.equals(this.subComponent, other.subComponent)) {
//            return false;
//        }
//        return true;
//    }
}
