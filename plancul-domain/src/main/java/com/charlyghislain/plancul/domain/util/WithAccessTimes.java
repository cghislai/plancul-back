package com.charlyghislain.plancul.domain.util;

import java.time.LocalDateTime;

public interface WithAccessTimes {

    LocalDateTime getCreated();

    LocalDateTime getUpdated();
}
