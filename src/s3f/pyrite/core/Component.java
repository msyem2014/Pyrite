/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.core;

import java.util.Arrays;
import java.util.List;
import s3f.pyrite.util.Vector;

/**
 *
 * @author antunes
 */
public class Component extends Fixable {

    public Object whut = null;
    private static int ID = 0;
    //id
    private int uid;
    public Component previous;
    public int distance;
    private String name;
    private Object data;
    private Vector pos;
    private boolean coupler = false;
    private java.util.Vector<Connection> conns;
    private java.util.Vector<Component> shortcut;
    private Object property;

    public Component(String name, Object data) {
        uid = ID++;
        conns = new java.util.Vector<>();
        shortcut = new java.util.Vector<>();
        pos = new Vector(100);//random
        this.name = name;
        this.data = data;
    }

    public Component(String name) {
        this(name, null);
    }

    public Component() {
        this("", null);
        coupler = true;
    }

    public <T> T getProperty() {
        return (T) property;
    }

    public void setProperty(Object p) {
        property = p;
    }

    public Connection createConnection(Component c) {
        return new Connection(this, c);
    }

    @Deprecated
    public void addConnection(Connection c) {
        if (!conns.contains(c)) {
            conns.add(c);
        }
    }

    public void removeConnection(Connection c) {
        conns.remove(c);
    }

    public Connection getConnection(Component b) {
        for (Connection c : conns) {
            if (c.getOtherComponent(this) == b) {
                return c;
            }
        }
        return null;
    }

    public List<Connection> getConnections() {
        return conns;
    }

    public Connection getOtherConnection(Connection c) {
        if (conns.size() == 2) {
            if (conns.get(0) == c) {
                return conns.get(1);
            } else {
                return conns.get(0);
            }
        }
        return null;
    }

    public void addShortcut(Component c) {
        if (!shortcut.contains(c)) {
            shortcut.add(c);
            if (!c.getShortcuts().contains(this)) {
                c.getShortcuts().add(this);
            }
        }
    }

    public void removeShortcut(Component c) {
        shortcut.remove(c);
    }

    public List<Component> getShortcuts() {
        return shortcut;
    }

    public Vector getPos() {
        return pos;
    }

    public void setPos(Vector pos) {
        this.pos.setPos(pos);
    }

    public boolean isCoupler() {
        return coupler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        coupler = false;
    }

    @Deprecated
    public void setCoupler(boolean coupler) {
        this.coupler = coupler;
    }

    public int getID() {
        return uid;
    }

    public String getUID() {
        return uid + (name.isEmpty() ? "" : "(" + name + ")");
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return (coupler ? "+'{" : "+{") + uid + " |" + name + ", '" + data + "', " + pos + ", c = " + conns.size() + ", s = " + shortcut.size() + "}" + "." + whut + ".";
    }

    public Connection appendAndConsume(Component c) {
        if (c.coupler) {
            Connection old = c.getConnection(this);
            int ter = 0;
            if (old != null) {
                c.removeConnection(old);
                this.removeConnection(old);
                old.setConsumed(true);
                ter = old.getTerminal(this);
            } else {
                System.err.println("WARNING: no connection");
            }
            for (Connection con : c.getConnections()) {
                con.replace(c, this);
                con.setTerminal(this, ter);
                conns.add(con);
            }
            c.conns.clear();
            c.setConsumed(true);
            return old;

//            this.FIXED_terminals.addAll(c.FIXED_terminals);
//            this.FIXED_connections.addAll(c.FIXED_connections);
//            this.FIXED_subComponents.addAll(c.FIXED_subComponents);
//            this.FIXED_doneConnections.addAll(c.FIXED_doneConnections);
//            this.fixed = true;
        } else {
            throw new IllegalArgumentException("this or c is not an contact");
        }
    }

    public Component copy() {
        Component nc = new Component();
        nc.uid = uid;
        nc.name = name;
        nc.data = data;
        nc.pos = new Vector(pos);
        nc.coupler = coupler;
        nc.whut = whut;
        return nc;
    }
}
