package com.kcz.common;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by kcz on 2017/7/17.
 */
@Data
public class AccessToken implements Serializable {
		private static final long serialVersionUID = -8022552483271692976L;
		private String token;
		private int expiresIn;
}
