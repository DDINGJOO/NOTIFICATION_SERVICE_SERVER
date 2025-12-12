package com.teambind.springproject.domain.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailConfirmEvent {
	
	private String email;
	private String code;
}
