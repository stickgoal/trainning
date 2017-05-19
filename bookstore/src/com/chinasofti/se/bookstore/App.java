package com.chinasofti.se.bookstore;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 启动应用
 * Created by Lucas on 2017-05-18.
 */
public class App {

    private static final List<Book> books = new ArrayList<>();

    private static final FileOperation fileOperation = new FileOperationImpl();
    public static final String BOOK_FILE_NAME = "book.txt";

    private Scanner scanner = new Scanner(System.in);

    //使用静态块初始化文件
    static{
        books.add(new Book("Java，从入门到精通","100x",new BigDecimal("58.5"),3000));
        books.add(new Book("MySQL，从入门到放弃","200x",new BigDecimal("15.535"),2000));
        books.add(new Book("Web，从陌路到陌路","300x",new BigDecimal("35.5"),20000));
        fileOperation.initSave("book.txt",books);

    }


    public static void main(String[] args) {
        App app = new App();
        app.shopping();

    }

    public void shopping(){

        Map<Book,Integer> cart = new HashMap<>();


        println(">>心灵阶梯书店<<");
        println("1.前台购买");
        println("2.后台录入");
        int fb = scanner.nextInt();
        if(fb==1) {
            println("您可以选择要购买的图书，一次一本");

            List<Book> books = fileOperation.load(BOOK_FILE_NAME);

            buy(cart, books);

            println("您购买的书籍列表如下：");

            BigDecimal total = new BigDecimal(0);

            for (Map.Entry<Book, Integer> e : cart.entrySet()) {
                Book book = e.getKey();
                Integer count = e.getValue();
                println(book.getName() + "\t" + book.getPrice() + "\t" + count);
                total = total.add(book.getPrice().multiply(new BigDecimal(count)));
            }

            newSeparator();

            println("共计：" + total + "元,确认支付?[y/n]");
            String pay = scanner.next();
            if (pay.equalsIgnoreCase("y")) {
                println("您已支付" + total + "元，感谢您的光临，欢迎下次惠顾！");
            }
        }else if(fb==2){
            println("欢迎您来到后台系统，请按提示操作");
            List<Book> newBooks = new ArrayList<>();
            registerBook(newBooks);
            fileOperation.save(BOOK_FILE_NAME,newBooks);
            println("数据录入完成，自动回到首页");
            shopping();
        }
    }

    private void registerBook(List<Book> newBooks) {
        Book book = new Book();
        println("请输入书名：");
        book.setName(scanner.next());
        println("请输入ISBN号：");
        book.setISBN(scanner.next());
        println("请输入价格：");
        book.setPrice(new BigDecimal(scanner.next()));
        println("请输入库存量：");
        book.setStock(scanner.nextInt());
        newBooks.add(book);
        println("输入完毕，需要继续吗？[y/n]");
        String yn = scanner.next();
        if(yn.equalsIgnoreCase("y")){
            registerBook(newBooks);
        }
    }

    private void buy(Map<Book, Integer> cart, List<Book> books) {
        //java8 stream操作
        List<String> bookNames = books.stream().map(Book::getName).collect(Collectors.toList());

        for(int i =0;i<bookNames.size();i++){
            println(i+". "+bookNames.get(i));
        }

        println("输入需要查看的书籍序号");
        int idx = scanner.nextInt();
        Book book = books.get(idx);
        newSeparator();
        println("书名："+book.getName());
        println("ISBN："+book.getISBN());
        println("价格："+book.getPrice().toPlainString());
        println("库存："+book.getStock());
        newSeparator();
        println("是否购买？[y/n]");
        String confirm = scanner.next();
        if(confirm.equalsIgnoreCase("y")){
            doBuy(cart, books, idx, book);
        }
    }

    private void doBuy(Map<Book, Integer> cart, List<Book> books, int idx, Book book) {
        println("您需要购买多少本？");
        int count = scanner.nextInt();
        if(count>book.getStock()){
            println("抱歉数量不够，最多只能购买"+book.getStock()+"本");
            doBuy(cart,books,idx,book);
        }else{
            cart.put(book,count);
            books.remove(idx);
            println("继续购买？[y/n]");
            String isContinue = scanner.next();
            if(isContinue.equalsIgnoreCase("y")){
                buy(cart,books);
            }

        }
    }

    static void println(String content){
        print(content);
        System.out.println();
    }
    static void print(String content){
        System.out.print(">"+content);
    }

    static void newSeparator(){
        println("===================================");
    }


}
