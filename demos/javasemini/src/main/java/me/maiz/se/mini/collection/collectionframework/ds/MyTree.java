package me.maiz.se.mini.collection.collectionframework.ds;

/**
 * 搜索二叉树
 */
public class MyTree {

        private class Node{
            int key;
            int value;
            Node left;
            Node right;

            Node(int key,int value){
                this.key = key;
                this.value = value;
                left = null;
                right = null;
            }
        }

        public void preOrder(Node subtreeRoot){
            if(subtreeRoot != null){
                preOrder(subtreeRoot.left);
                System.out.print(" "+subtreeRoot+" ");
                preOrder(subtreeRoot.right);
            }
        }


        private Node root;
        public MyTree() {
            root = null;
        }
        //插入，定位的逻辑根节点大于左子树，小于右子树
        public void insert(int key,int value){
            Node newNode = new Node(key,value);
            if(root == null){
                root = newNode;
            }else{
                Node current = root;
                while(true){
                    if(newNode.key<current.key){
                        current = current.left;
                    }else{
                        current = current.right;
                    }
                    if(current == null){
                        current = newNode;
                        return;
                    }
                }
            }
        }
        private Node getNode(int key) throws Exception{
            Node result = root;
            while(result.key != key){
                if(key < result.key){
                    result = result.left;
                }else{
                    result = result.right;
                }
                if(result == null){
                    throw new Exception("Can't find value by "+key);
                }
            }
            return result;

        }
        public int getValue(int key) throws Exception{
            return getNode(key).value;
        }
        public void delete(int key) throws Exception{
            Node current = root;
            Node parent = null;
            while(current.key != key){
                parent = current;
                if(key < current.key){
                    current = current.left;
                }else{
                    current = current.right;
                }
                if(current == null){
                    throw new Exception("Can't find value by "+key);
                }
            }
            if(parent.left == current){
                parent.left = current.left;
                Node temp = current;
                while(temp.right != null){
                    temp = temp.right;
                }
                temp.right = current.right;
            }else{
                parent.right = current.right;
                Node temp = current;
                while(temp.left != null){
                    temp = temp.left;
                }
                temp.left = current.left;
            }
        }
        /**
         * @param args
         */
        public static void main(String[] args) {
            // TODO Auto-generated method stub
            MyTree mt = new MyTree();
            mt.insert(1,10);
            mt.insert(3,30);
            mt.insert(8,80);
            mt.insert(2,20);
            mt.insert(5,50);
            try {
                System.out.println(mt.getValue(8));
            } catch (Exception e) {
                // TODO: wash exception
                e.printStackTrace();
            }
        }

    }

