package org.nenad.paunov.config.annotations;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
@Parameter(in = ParameterIn.QUERY, name = "page", description = "Results page you want to retrieve (0..N).", schema = @Schema(defaultValue = "0", example = "1"))
@Parameter(in = ParameterIn.QUERY, name = "size", description = "Number of records per page.", schema = @Schema(defaultValue = "20", example = "15"))
@Parameter(in = ParameterIn.QUERY, name = "sort", description = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", schema = @Schema(example = "name,DESC"), allowEmptyValue = true)
public @interface ApiPageable {
}
