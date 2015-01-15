package com.room.raindrops.receivers;

import java.io.Serializable;

public interface WebServiceListener extends Serializable {
	public void onError(final int responseCode, final String errorMessage);
	public void onResult(final String response);
}
