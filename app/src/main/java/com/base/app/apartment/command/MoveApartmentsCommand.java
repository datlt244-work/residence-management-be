package com.base.app.apartment.command;

import java.util.List;

public record MoveApartmentsCommand(List<String> apartmentIds, String targetZoneId, String targetApartmentTypeId) {}
