package service;

import com.override.models.Recipient;
import com.override.service.MessageService;
import com.override.service.RecipientService;
import com.override.util.CommunicationStrategy;
import com.override.util.CommunicationStrategyFactory;
import com.override.util.EmailCommunication;
import com.override.util.TelegramCommunication;
import enums.Communication;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityExistsException;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static utils.TestFieldsUtil.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private RecipientService recipientService;

    @Mock
    private TelegramCommunication telegramCommunication;

    @Mock
    private EmailCommunication emailCommunication;

    @Mock
    private CommunicationStrategyFactory strategyFactory;

    @Test
    public void testWhenSendMessage() {
        Recipient recipient = getRecipient();
        String message = "test";
        Communication[] communication = {Communication.TELEGRAM, Communication.EMAIL};
        Map<Communication, CommunicationStrategy> strategyMap = getSenderMap(telegramCommunication, emailCommunication);

        when(recipientService.findRecipientByLogin(recipient.getLogin())).thenReturn(recipient);
        when(strategyFactory.getSenderMap()).thenReturn(strategyMap);
        doNothing().when(telegramCommunication).sendMessage(recipient, message);

        messageService.sendMessage(recipient.getLogin(), message, communication);

        verify(recipientService, times(1)).findRecipientByLogin(recipient.getLogin());
        verify(strategyFactory, times(1)).getSenderMap();
        verify(telegramCommunication, times(1)).sendMessage(recipient, message);
    }

    @Test
    public void testWhenCommunicationTypesEmpty() {
        Recipient recipient = getRecipient();
        String message = "test";

        assertThrows(InvalidParameterException.class, () -> messageService.sendMessage(recipient.getLogin(), message));
    }

    @Test
    public void testWhenRecipientNotFound() {
        String message = "test";
        Communication[] communication = {Communication.TELEGRAM, Communication.EMAIL};

        when(recipientService.findRecipientByLogin("test")).thenThrow(NoSuchElementException.class);

        assertThatThrownBy(() -> messageService.sendMessage("test", message, communication))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void testWhenFirstTypeCommunicationReturnError() {
        Recipient recipient = getRecipient();
        String message = "test";
        Communication[] communication = {Communication.TELEGRAM, Communication.EMAIL};
        Map<Communication, CommunicationStrategy> strategyMap = getSenderMap(telegramCommunication, emailCommunication);

        when(recipientService.findRecipientByLogin(recipient.getLogin())).thenReturn(recipient);
        when(strategyFactory.getSenderMap()).thenReturn(strategyMap);
        doThrow(FeignException.class).when(telegramCommunication).sendMessage(recipient, message);
        doNothing().when(emailCommunication).sendMessage(recipient, message);

        messageService.sendMessage(recipient.getLogin(), message, communication);

        verify(recipientService, times(1)).findRecipientByLogin(recipient.getLogin());
        verify(strategyFactory, times(1)).getSenderMap();
        assertThatThrownBy(() -> telegramCommunication.sendMessage(recipient, message))
                .isInstanceOf(FeignException.class);
        verify(emailCommunication, times(1)).sendMessage(recipient, message);
    }
}