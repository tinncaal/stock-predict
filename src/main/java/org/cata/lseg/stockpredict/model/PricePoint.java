package org.cata.lseg.stockpredict.model;

import java.time.LocalDate;

public record PricePoint(LocalDate date, double price){}
