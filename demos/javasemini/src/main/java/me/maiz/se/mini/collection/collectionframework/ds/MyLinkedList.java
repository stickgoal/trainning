package me.maiz.se.mini.collection.collectionframework.ds;


public class MyLinkedList {
    private class Node {

        private int key;
        private int value;
        private Node next;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
            next = null;
        }
    }

    private Node head;

    public MyLinkedList() {

        head = null;
    }

    public void addNode(int key, int value) {
        if (head == null) {
            head = new Node(key, value);
        } else {
            //从头部开始遍历，定位到最后一个节点
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            //将最后节点的下一个节点设置为新节点
            current.next = new Node(key, value);
        }
    }

    public boolean delNode(int key) {
        Node current = head;
        Node previous;
        //头节点的特殊处理
        if (head == null) {
            return false;
        }
        if (head.key == key) {
            head = head.next;
            return true;
        }
        //从头节点的下一个节点开始遍历，定位到要删除的节点
        current = head.next;
        previous = head;
        while (current.key != key) {
            //遍历到尾部时，next为null，此时没有找到该对象
            if (current.next == null) {
                break;
            }
            current = current.next;
            previous = current;
        }
        //尾节点,无法删除
        if (current.next == null) {
            return false;
        } else {
            //非尾节点，将前节点连上后节点
            previous.next = current.next;
            return true;
        }
    }

    private Node getNode(int key) {
        Node current = head;
        if (head.key == key) {
            return head;
        } else {
            current = head.next;
            while (current.key != key) {
                current = current.next;
            }
            return current;
        }

    }

    public int getValue(int key) {
        return getNode(key).value;
    }

    private void view() {
        Node current = head;
        while (current != null) {
            System.out.println(current.key + "  " + current.value);
            current = current.next;
        }
    }


    public boolean insertNode(Node newNode, int index) {
        Node preNode = head;
        if (preNode == null || index == 0) {
            newNode.next = preNode;
            preNode = newNode;
            return true;
        } else {
            while (preNode.next != null && index-- > 0) {
                preNode = preNode.next;
            }
            newNode.next = preNode.next;
            preNode.next = newNode;
            return true;
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        MyLinkedList ml = new MyLinkedList();
        ml.addNode(1, 89);
        ml.addNode(2, 84);
        ml.addNode(3, 895);
        ml.addNode(4, 896);
        ml.delNode(3);
        ml.view();
        System.out.println("getValue:" + ml.getValue(2));
    }

}

