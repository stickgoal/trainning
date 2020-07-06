package me.maiz.trainning.project.flashsale.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "emall_flashsale")
public class FlashSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int flashSaleId;
    /**
     * 开始时间
     */
    private Date beginTime;
    /**
     * 结束时间
     */
    private Date endTime;


}
