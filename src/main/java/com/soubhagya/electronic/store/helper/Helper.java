package com.soubhagya.electronic.store.helper;

import com.soubhagya.electronic.store.dtos.PageableResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class that assists with the conversion of paginated entity data
 * to paginated DTO data.
 *
 * This class provides functionality to transform a {@code Page} of entity
 * objects into a {@code PageableResponse} containing DTO objects. The conversion
 * is handled using ModelMapper, which maps the properties of each entity object
 * to a DTO of the specified type. Paginated metadata from the entity page is
 * also transferred to the DTO response, preserving pagination details such as
 * page number, page size, total elements, total pages, and whether the page is
 * the last one.
 */
//U = entity, V = dto
public class Helper {

    private static final ModelMapper modelMapper = new ModelMapper();

    /**
     * Converts a Spring Data Page of entity objects into a PageableResponse of DTO objects.
     *
     * This method uses ModelMapper to map each entity object from the input page to a DTO of a specified type.
     * It transfers all pagination information such as page number, page size, total elements, total pages,
     * and last page status from the entity page to the resulting DTO PageableResponse.
     *
     * @param <U> the type of entity contained in the source page
     * @param <V> the type of DTO to be contained in the resulting PageableResponse
     * @param page the Page of entity objects to be converted
     * @param type the class type of the target DTO objects
     * @return a PageableResponse containing the mapped DTO objects along with pagination data
     */
    public static <U,V> PageableResponse<V> getPageableResponse(Page<U> page, Class<V> type){
        List<U> entity = page.getContent();
        List<V> dtoList = entity.stream()
                .map(object -> modelMapper.map(object, type))
                .collect(Collectors.toList());

        PageableResponse<V> response = new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());

        return response;
    }
}
