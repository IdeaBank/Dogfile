package com.honeyosori.dogfile.global.klat.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KlatComponent {
    @Value("${klat.app_id}")
    private String KLAT_APP_ID;
    @Value("${klat.api_key}")
    private String KLAT_API_KEY;
}
