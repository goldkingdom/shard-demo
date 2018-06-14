package cn.xj.common.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Stream {

    String NODEINFOOUT = "nodeInfoOut";
    String NODEINFOIN = "nodeInfoIn";
    String QUERYOUT = "queryOut";
    String QUERYIN = "queryIn";
    String QUERIEDOUT = "queriedOut";
    String QUERIEDIN = "queriedIn";
    String MODIFYOUT = "modifyOut";
    String MODIFYIN = "modifyIn";
    String MODIFIEDOUT = "modifiedOut";
    String MODIFIEDIN = "modifiedIn";

    @Output(Stream.NODEINFOOUT)
    MessageChannel nodeInfoOut();

    @Input(Stream.NODEINFOIN)
    SubscribableChannel nodeInfoIn();

    @Output(Stream.QUERYOUT)
    MessageChannel queryOut();

    @Input(Stream.QUERYIN)
    SubscribableChannel queryIn();

    @Output(Stream.QUERIEDOUT)
    MessageChannel queriedOut();

    @Input(Stream.QUERIEDIN)
    SubscribableChannel queriedIn();

    @Output(Stream.MODIFYOUT)
    MessageChannel modifyOut();

    @Input(Stream.MODIFYIN)
    SubscribableChannel modifyIn();

    @Output(Stream.MODIFIEDOUT)
    MessageChannel modifiedOut();

    @Input(Stream.MODIFIEDIN)
    SubscribableChannel modifiedIn();

}
