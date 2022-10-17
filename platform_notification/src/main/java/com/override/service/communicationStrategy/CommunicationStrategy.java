package com.override.service.communicationStrategy;

import com.override.model.Recipient;
import enums.Communication;

public interface CommunicationStrategy {
    void sendMessage(Recipient recipient, String message);

    Recipient updateRecipient(Recipient recipient, String value);

    Communication getType();
}
