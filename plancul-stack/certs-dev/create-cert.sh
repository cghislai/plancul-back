#!/usr/bin/env bash


 docker run --rm \
 -e SUBJ_ALT_NAMES="localhost plancul-front.docker plancul-front.docker" \
 -e SUBJ_IPS="10.0.2.2" \
 -e SELF_SIGNED_OUT_DIR=/etc/ssl/selfsigned/ \
 --entrypoint '' \
 -v $(pwd):/etc/ssl/selfsigned/ \
 docker.valuya.be/proxy:14 \
 /create-certificates-signed-by-authority.sh "localhost"
