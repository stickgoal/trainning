package me.maiz.demo.moderntech.lombok;


import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Order {

    @NonNull
    private String orderNO;
    @NonNull
    private Date orderTime;

    private String memo;


}
