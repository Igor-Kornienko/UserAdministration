package kornienko.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AjaxResponseBody {
    @JsonView
    String msg;

    @JsonView
    Integer code;

    @JsonView
    List<Object> result;
}
