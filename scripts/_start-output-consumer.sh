#!/bin/bash -eux

# consumes what our kafka streams word count stuff produces
cd $HOME/kafka_2.12-2.5.0
echo "Starting consumer for streams-wordcount-output"
# see here for how to set up the deserializer in cli 
# http://mail-archives.apache.org/mod_mbox/kafka-users/201709.mbox/%3CCAMdhPwRYRBg-LKbS_z3Ar5m0BkpLU8=gFR1E=yyh-z_Psa3s1g@mail.gmail.com%3E
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic streams-wordcount-output --from-beginning \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
