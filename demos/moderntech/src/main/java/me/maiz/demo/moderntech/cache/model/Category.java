package me.maiz.demo.moderntech.cache.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

	private int categoryId;

	private String categoryName;

	private int userId;

	private boolean isDefault;



}
