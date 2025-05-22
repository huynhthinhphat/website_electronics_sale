package com.tip.b18.electronicsales.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum VietnameseCarrier {
    VIETTEL(Set.of("086", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039")),
    VINAPHONE(Set.of("088", "091", "094", "081", "082", "083", "084", "085")),
    MOBIFONE(Set.of("089", "090", "093", "070", "076", "077", "078", "079")),
    VIETNAMOBILE(Set.of("092", "056", "058")),
    GMOBILE(Set.of("099", "059"));

    private final Set<String> prefixes;
}
