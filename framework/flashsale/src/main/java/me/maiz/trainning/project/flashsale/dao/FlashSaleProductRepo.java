package me.maiz.trainning.project.flashsale.dao;

import me.maiz.trainning.project.flashsale.entity.FlashSale;
import me.maiz.trainning.project.flashsale.entity.FlashSaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlashSaleProductRepo extends JpaRepository<FlashSaleProduct,Integer> {


    List<FlashSaleProduct> findAllByFlashSaleId(int flashSaleId);


}
