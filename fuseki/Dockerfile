FROM hansidm/fuseki:4.7.0

RUN mkdir /fuseki/configuration
COPY fuseki-jetty.xml hascoapi_assembler.ttl /fuseki/configuration/

CMD [ \
    "--config=/fuseki/configuration/hascoapi_assembler.ttl", \
    "--jetty=/fuseki/configuration/fuseki-jetty.xml", \
    "--ping", \
    "--stats", \
    "--metrics", \
    "--compact" \
]
