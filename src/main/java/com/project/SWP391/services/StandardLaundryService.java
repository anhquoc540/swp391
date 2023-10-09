package com.project.SWP391.services;

import com.project.SWP391.entities.PriceBasedWeight;
import com.project.SWP391.entities.SpecialLaundry;
import com.project.SWP391.entities.StandardLaundry;
import com.project.SWP391.repositories.PriceRepository;
import com.project.SWP391.repositories.StandardServiceRepository;
import com.project.SWP391.repositories.StoreRepository;
import com.project.SWP391.requests.SpecialServiceRequest;
import com.project.SWP391.requests.StandardServiceRequest;
import com.project.SWP391.responses.dto.PriceInWeightDTO;
import com.project.SWP391.responses.dto.SpecialServiceInfoDTO;
import com.project.SWP391.responses.dto.StandardServiceInfoDTO;
import com.project.SWP391.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandardLaundryService {
    private  final StoreRepository storeRepository;
    private final PriceRepository priceRepository;
    private final StandardServiceRepository serviceRepository;

    @Autowired
    private final ModelMapper mapper;
    public List<StandardServiceInfoDTO> getAllStandardServiceForCustomer(){

        List<StandardLaundry> laundries = serviceRepository.findAll();
        Predicate<StandardLaundry> byDeleted = specialLaundry -> specialLaundry.getIsDeleted() == 0;
        return  laundries.stream().filter(byDeleted).map(service -> mapToDTO(service)).collect(Collectors.toList());

    }

    public StandardServiceInfoDTO getStandardServiceForCustomer(Long id){
        StandardLaundry laundries = serviceRepository.findById(id).orElseThrow();
        return  mapToDTO(laundries);

    }

    public StandardServiceInfoDTO getStandardServiceForStore(){

        var store = storeRepository.findStoreByUserId(SecurityUtils.getPrincipal().getId());
        var laundry = serviceRepository.findByStoreId(store.getId());
        return  mapToDTO(laundry);

    }

    public StandardServiceInfoDTO createStandardService(StandardServiceRequest request){
        var store = storeRepository.findStoreByUserId(SecurityUtils.getPrincipal().getId());
        Set<PriceBasedWeight> prices = request.getPrices_weight();


        var service = StandardLaundry.builder().store(store)
                .name(request.getName())
                .isDeleted(0)
                .description(request.getDescription())
                .prices_weight(prices)
                .build();
        var newService = serviceRepository.save(service);
        Set<PriceBasedWeight> newPrices = service.getPrices_weight().stream().peek(priceBasedWeight -> priceBasedWeight.setStandardLaundry(service)).collect(Collectors.toSet());
        var savePrice = priceRepository.saveAll(newPrices);
        return mapToDTO(newService);
    }

    public StandardServiceInfoDTO updateStandardService(StandardServiceRequest request, long id) {
        var editStandardService = serviceRepository.findById(id).orElseThrow();
        Set<PriceBasedWeight> newPrices = request.getPrices_weight();
        var prices = priceRepository.findAllByStandardLaundryId(editStandardService.getId());
        for (var item: prices
             ) {
            for (var newItem: newPrices
                 ) {
                if(item.getId() == newItem.getId()){
                    item.setPrice(newItem.getPrice());
                    item.setFrom(newItem.getFrom());
                    item.setTo(newItem.getTo());
                }

            }

        }
        if(editStandardService.getIsDeleted() == 1){
            throw new RuntimeException("Service is not found");
        }
        editStandardService.setName(request.getName());
        editStandardService.setDescription(request.getDescription());
        var newService = serviceRepository.save(editStandardService);
        var savePrice = priceRepository.saveAll(prices);
        return  mapToDTO(newService);

    }



    public StandardServiceInfoDTO deleteSpecialService(long id) {
        var editSpecialService = serviceRepository.findById(id).orElseThrow();

        editSpecialService.setIsDeleted(1);

        var newService = serviceRepository.save(editSpecialService);
        return  mapToDTO(newService);

    }


    private StandardServiceInfoDTO mapToDTO(StandardLaundry dto) {
        return mapper.map(dto, StandardServiceInfoDTO.class);
    }

}