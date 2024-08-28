#
# COPYRIGHT Ericsson 2023-2024
#
#
#
# The copyright to the computer program(s) herein is the property of
#
# Ericsson Inc. The programs may be used and/or copied only with written
#
# permission from Ericsson Inc. or in accordance with the terms and
#
# conditions stipulated in the agreement/contract under which the
#
# program(s) have been supplied.
#

FROM armdocker.rnd.ericsson.se/proj-esoa-so/so-base-openjdk17:1.3.2-1
COPY ./eric-esoa-subsystem-management-service/target/eric-esoa-subsystem-management-service.jar subsystem-management.jar
COPY entrypoint.sh /entrypoint.sh

EXPOSE 8080

ENV JAVA_OPTS=""
ENV ADP_KMS_CACERT_FILE_PATH=""
ENV LOADER_PATH=""
ENV DEFAULT_JAVA_CACERTS="/var/lib/ca-certificates/java-cacerts"
ENV JAVA_KEYSTORE_PW="changeit"

RUN zypper --non-interactive in catatonit

ARG user=appuser
ARG uid=186362
ARG gid=186362
ARG COMMIT
ARG BUILD_DATE
ARG APP_VERSION
ARG RSTATE
ARG IMAGE_PRODUCT_NUMBER

LABEL \
    org.opencontainers.image.title="subsystem-management-service" \
    org.opencontainers.image.created=$BUILD_DATE \
    org.opencontainers.image.revision=$COMMIT \
    org.opencontainers.image.vendor=Ericsson \
    org.opencontainers.image.version=$APP_VERSION \
    com.ericsson.base-image.product-name="Common Base OS SLES IMAGE" \
    com.ericsson.product-revision="${RSTATE}" \
    com.ericsson.product-number="$IMAGE_PRODUCT_NUMBER"

ENTRYPOINT ["/usr/bin/catatonit", "--"]

# DR-D1123-122: The Containers Shall Have a Non-Login Numeric User Identity.
RUN echo "${user}:x:${uid}:${gid}:appuser:/:/bin/bash" >> /etc/passwd \
    && sed -i 's/^\(root.*\):.*/\1:\/bin\/false/' /etc/passwd && sed -i 's/^root:/root:!/' /etc/shadow
RUN chown $uid:$gid "${DEFAULT_JAVA_CACERTS}" && chmod +w "${DEFAULT_JAVA_CACERTS}" && chmod 777 "/entrypoint.sh"

CMD ["sh", "-c", "/entrypoint.sh"]
