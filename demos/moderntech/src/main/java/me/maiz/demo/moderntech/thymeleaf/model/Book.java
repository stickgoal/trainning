package me.maiz.demo.moderntech.thymeleaf.model;

import java.util.Date;

public class Book {
	private int bookId;
	
	private String bookName;
	
	private String author;
	
	private String translator;
	
	private String shortDesc;
	
	private String introduction;
	
	private String publisher;
	
	private Date publishTime;
	
	private double price;
	
	private double officalPrice;
	
	private String bookFormat;
	
	private String paperMaterial	;
	
	private String packageType;
	
	private boolean isSuit;
	
	private String isbn;
	
	private String mainImg;
	
	private String imgs;
	
	private String featureImg;

	public Book() {
	}

	public Book(int bookId, String bookName, String author, String publisher, double price, double officalPrice) {
		this.bookId = bookId;
		this.bookName = bookName;
		this.author = author;
		this.publisher = publisher;
		this.price = price;
		this.officalPrice = officalPrice;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getOfficalPrice() {
		return officalPrice;
	}

	public void setOfficalPrice(double officalPrice) {
		this.officalPrice = officalPrice;
	}

	public String getBookFormat() {
		return bookFormat;
	}

	public void setBookFormat(String bookFormat) {
		this.bookFormat = bookFormat;
	}

	public String getPaperMaterial() {
		return paperMaterial;
	}

	public void setPaperMaterial(String paperMaterial) {
		this.paperMaterial = paperMaterial;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public boolean isSuit() {
		return isSuit;
	}

	public void setSuit(boolean isSuit) {
		this.isSuit = isSuit;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getMainImg() {
		return mainImg;
	}

	public void setMainImg(String mainImg) {
		this.mainImg = mainImg;
	}

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public String getFeatureImg() {
		return featureImg;
	}

	public void setFeatureImg(String featureImg) {
		this.featureImg = featureImg;
	}

	@Override
	public String toString() {
		return "Book [bookId=" + bookId + ", bookName=" + bookName + ", author=" + author + ", translator=" + translator
				+ ", shortDesc=" + shortDesc + ", introduction=" + introduction + ", publisher=" + publisher
				+ ", publishTime=" + publishTime + ", price=" + price + ", officalPrice=" + officalPrice
				+ ", bookFormat=" + bookFormat + ", paperMaterial=" + paperMaterial + ", packageType=" + packageType
				+ ", isSuit=" + isSuit + ", isbn=" + isbn + ", mainImg=" + mainImg + ", imgs=" + imgs + ", featureImg="
				+ featureImg + "]";
	}
	
}
