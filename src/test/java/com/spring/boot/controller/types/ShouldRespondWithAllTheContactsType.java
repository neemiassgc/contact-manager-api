package com.spring.boot.controller.types;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class ShouldRespondWithAllTheContactsType {

    final String username;
    final int phoneNumberSize;
    final int addressesSize;
    final int emailsSize;
    final String[] names;
}
