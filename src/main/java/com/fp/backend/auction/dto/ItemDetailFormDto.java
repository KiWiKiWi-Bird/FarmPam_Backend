package com.fp.backend.auction.dto;

import com.fp.backend.auction.entity.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class ItemDetailFormDto {
    private Long id;

    private String itemTitle;

    private Integer minPrice;

    private String itemDetail;

    private long time;

    private String city;

    private int weight;

    private Boolean isSoldout;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    private List<String> tagNames;

    private static ModelMapper modelMapper = new ModelMapper();

    public static ItemDetailFormDto of(Item item) {
        ItemDetailFormDto itemDetailFormDto = modelMapper.map(item, ItemDetailFormDto.class);

        // 경매 마감 시간
        long currentTime = System.currentTimeMillis();
        long elapsedTime = item.getTime() - currentTime;
        if (elapsedTime > 0) {
            itemDetailFormDto.setTime(elapsedTime);
        } else {
            itemDetailFormDto.setTime(0);
            itemDetailFormDto.setIsSoldout(true);
        }

        return itemDetailFormDto;
    }

}
