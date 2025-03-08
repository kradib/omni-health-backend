package com.example.omni_health_app.calculator;

import com.example.omni_health_app.domain.model.AppointmentSlotCounts;
import com.example.omni_health_app.domain.model.Slot;
import com.example.omni_health_app.dto.model.AppointmentSlotAvailable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.*;

@Component
@Slf4j
public class AppointmentSlotCountCalculator {


    private static final Map<Integer, Slot> slot_mapping = Map.of(
            1, Slot.builder()
                    .id(1)
                    .time("09:00 AM")
                    .build(),
            2, Slot.builder()
                    .id(2)
                    .time("10:00 AM")
                    .build(),
            3, Slot.builder()
                    .id(3)
                    .time("11:00 AM")
                    .build(),
            4, Slot.builder()
                    .id(4)
                    .time("12:00 PM")
                    .build(),
            5, Slot.builder()
                    .id(5)
                    .time("01:00 PM")
                    .build(),
            6, Slot.builder()
                    .id(6)
                    .time("02:00 PM")
                    .build(),
            7, Slot.builder()
                    .id(7)
                    .time("03:00 PM")
                    .build(),
            8, Slot.builder()
                    .id(8)
                    .time("04:00 PM")
                    .build(),
            9, Slot.builder()
                    .id(9)
                    .time("05:00 PM")
                    .build());

    @Value("${omni.slot-booking}")
    private Integer availableSlots;

    public List<AppointmentSlotAvailable> calculateAvailableSlot(List<AppointmentSlotCounts> appointmentSlotCounts) {
        log.info("appointmentSlotCounts: {}", appointmentSlotCounts);
        final List<AppointmentSlotAvailable> appointmentSlotAvailableList = new LinkedList<>();
        slot_mapping.keySet().forEach(slotId -> {
            Optional<AppointmentSlotCounts> appointmentSlotCountsOptional = appointmentSlotCounts.stream()
                    .filter(appointmentSlotCount -> appointmentSlotCount.getSlotId() == slotId)
                    .findFirst();
            if(appointmentSlotCountsOptional.isEmpty()) {
                appointmentSlotAvailableList.add(AppointmentSlotAvailable.builder()
                                .slot(slot_mapping.get(slotId))
                                .availableSLots(availableSlots)
                                .build());
            } else {
                appointmentSlotAvailableList.add(AppointmentSlotAvailable.builder()
                        .slot(slot_mapping.get(slotId))
                        .availableSLots(availableSlots - (int)appointmentSlotCountsOptional.get().getNumberOfAppointments())
                        .build());
            }
        });
        appointmentSlotAvailableList.sort(Comparator.comparingInt(o -> o.getSlot().getId()));

        return appointmentSlotAvailableList;
    }


}
