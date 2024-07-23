> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

**values.yaml**
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/referencias/values_yaml.md&amp;action_name=monitoracao/referencias/values_yaml)

```yaml
## Deploy prometheus
prometheus:
  enabled: true

## Setar o label que vai ser colocado em todos os artefatos. Ele deve ser igual a do serviceMonitorSelector mais abaixo.

  commonLabels:
    monitor: <sigla>  

  ## Habilitar o parametro "rulesInfra" em caso de federacao com o prometheus de infra
  defaultRules:
    prometheus: true
    create: true

    rulesAnnotations: {}
      #   clusterName: "monitoracao.pd.io.bb.com.br" #Identificação do cluster
    rulesLabels: {}
      # sigla: <SIGLA>
      # omi: sim
      # type: nuvem

    rules:
      prometheus: true
      app: false

  #   rulesInfra: true
## Declara as variaveis minimas a serem passadas para utilização da oferta do prometheus.
  ingress: 
    enabled: true  
    ## Hosts deve ser informado caso ingress seja habilitado.
    hosts: 
        - prometheus.<sigla>.desenv.bb.com.br
    ## Configuracao TLS para ingress do Prometheus
    ## Secret deve ser criado manualmente no namespace
    tls: [] 
      # - secretName: <certificado>-desenv-tls
      #   hosts:
      #     - <url>.<ambiente>.bb.com.br

  ## Prometheus StorageSpec para dados persistentes
  ## ref: https://github.com/coreos/prometheus /blob/master/Documentation/user-guides/storage.md

  
  prometheusSpec:
    storageSpec:  
      volumeClaimTemplate:
        spec:
          storageClassName: nas-client
          resources:
            requests:
              storage: 2Gi

    serviceMonitorSelector:
      matchLabels:
        sigla: <sigla>

    resources: 
      limits:
        cpu: 200m
        memory: 200Mi
      requests:
        cpu: 200m
        memory: 200Mi

    serviceMonitorNamespaceSelector:
      any: true

    ##Configuracao especifica para federacao com prometheus da infra
    additionalScrapeConfigs: []
    # - job_name: 'federate'
    #   scrape_interval: 15s
    #   honor_labels: true
    #   metrics_path: '/federate'
    #   params:
    #     'match[]':
    #       - '{namespace="<SEU NAMESPACE>"}' #Exemplo
    #   static_configs:
    #     - targets:
    #       - 'prometheus-data.psc-proxy.svc.cluster.local:9090'
    
grafana: 
  enabled: true

  ingress:
    enabled: true
    hosts:
     - monitor.<sigla>.desenv.bb.com.br
    tls: []
    #  - secretName: chart-example-tls
    #    hosts:
    #      - <url>.desenv.bb.com.br

  plugins:
    list: []
  ## Plugins disponíveis para instalação, apenas descomentar
        # - calheatmap/cal-heatmap
        # - carpetplot/carpet-plot
        # - citilogicsgeoloop/CitiLogics-citilogics-geoloop-panel-5ee83ae
        # - clockpanel/grafana-clock-panel-ac968f7
        # - datatable/briangann-grafana-datatable-panel-72dc7c5
        # - diagrampannel/jdbranham-grafana-diagram-4406897
        # - discrete/NatelEnergy-grafana-discrete-panel-5e8e975
        # - heatmap/savantly-net-grafana-heatmap-d2244ab
        # - mongodb/mongodb-grafana
        # - parityreport/zuburqan-grafana-parity-report-ec25fd6
        # - pictureit/vbessler-grafana-pictureit-b62fe2b
        # - piechart/pie-chart
        # - radar/snuids-grafana-radar-panel-d0ca5fe
        # - sentinl/sentinl-v6.4.2
        # - worldmappanel/worldmap-panel
        # - yesoreyeram/yesoreyeram-boomtable-panel
        # - flant/grafana-statusmap

  resources: 
    limits:
      cpu: 200m
      memory: 200Mi
    requests:
      cpu: 200m
      memory: 200Mi

  dashboards: 
    default:
  #    ## Dashboard padrão recursos de namespace
      recursosNS:
        enabled: false
  #    ## Dashboard padrão prometheus
      prometheus: 
        active: true
      4goldSignals:
        json: |-
          {
            "annotations": {
              "list": [
                {
                  "builtIn": 1,
                  "datasource": "-- Grafana --",
                  "enable": true,
                  "hide": true,
                  "iconColor": "rgba(0, 211, 255, 1)",
                  "name": "Annotations & Alerts",
                  "type": "dashboard"
                }
              ]
            },
            "editable": true,
            "gnetId": null,
            "graphTooltip": 0,
            "iteration": 1573674645726,
            "links": [],
            "panels": [
              {
                "collapsed": false,
                "gridPos": {
                  "h": 1,
                  "w": 24,
                  "x": 0,
                  "y": 0
                },
                "id": 33,
                "panels": [],
                "repeat": null,
                "title": "Gauges",
                "type": "row"
              },
              {
                "cacheTimeout": null,
                "colorBackground": true,
                "colorValue": false,
                "colors": [
                  "#890f02",
                  "#890f02",
                  "#3f6833"
                ],
                "datasource": "Prometheus-App",
                "description": "",
                "format": "none",
                "gauge": {
                  "maxValue": 100,
                  "minValue": 0,
                  "show": false,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 4,
                  "w": 12,
                  "x": 0,
                  "y": 1
                },
                "id": 37,
                "interval": "",
                "links": [],
                "mappingType": 2,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "maxPerRow": 6,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "DOWN",
                    "to": "null"
                  },
                  {
                    "from": "0",
                    "text": "DOWN",
                    "to": "0"
                  },
                  {
                    "from": "-99999",
                    "text": "DOWN",
                    "to": "0"
                  },
                  {
                    "from": "0",
                    "text": "UP",
                    "to": "999999"
                  }
                ],
                "repeat": null,
                "repeatDirection": "h",
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "#890f02",
                  "show": false
                },
                "tableColumn": "Value",
                "targets": [
                  {
                    "expr": "sum(up{namespace=~\"$Namespace\"})",
                    "format": "time_series",
                    "instant": true,
                    "intervalFactor": 2,
                    "legendFormat": "{{serviceVersion}}",
                    "refId": "A"
                  }
                ],
                "thresholds": "0, 1",
                "title": " ${Namespace}",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "N/A",
                    "value": "null"
                  },
                  {
                    "op": "=",
                    "text": "DOWN",
                    "value": "0"
                  },
                  {
                    "op": "=",
                    "text": "UP",
                    "value": "1"
                  },
                  {
                    "op": "=",
                    "text": "UP",
                    "value": "2"
                  },
                  {
                    "op": "=",
                    "text": "UP",
                    "value": "3"
                  },
                  {
                    "op": "=",
                    "text": "UP",
                    "value": "4"
                  },
                  {
                    "op": "=",
                    "text": "UP",
                    "value": "5"
                  },
                  {
                    "op": "=",
                    "text": "UP",
                    "value": "6"
                  },
                  {
                    "op": "=",
                    "text": "UP",
                    "value": "7"
                  },
                  {
                    "op": "=",
                    "text": "UP",
                    "value": "8"
                  },
                  {
                    "op": "=",
                    "text": "UP",
                    "value": "9"
                  }
                ],
                "valueName": "current"
              },
              {
                "cacheTimeout": null,
                "colorBackground": false,
                "colorValue": false,
                "colors": [
                  "rgba(50, 172, 45, 0.97)",
                  "rgba(237, 129, 40, 0.89)",
                  "rgba(245, 54, 54, 0.9)"
                ],
                "datasource": "Prometheus-App",
                "description": "Média de tráfego nos pods",
                "format": "reqps",
                "gauge": {
                  "maxValue": 90,
                  "minValue": 0,
                  "show": true,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 4,
                  "w": 6,
                  "x": 12,
                  "y": 1
                },
                "hideTimeOverride": false,
                "id": 35,
                "interval": "",
                "links": [],
                "mappingType": 1,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "pluginVersion": "6.3.5",
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "N/A",
                    "to": "null"
                  }
                ],
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "rgb(31, 120, 193)",
                  "show": false
                },
                "tableColumn": "",
                "targets": [
                  {
                    "dsType": "influxdb",
                    "expr": "avg(rate(http_requests_seconds_summary_count{namespace=~\"$Namespace\"}[$timerange])))",
                    "format": "time_series",
                    "groupBy": [
                      {
                        "params": [
                          "$__interval"
                        ],
                        "type": "time"
                      },
                      {
                        "params": [
                          "null"
                        ],
                        "type": "fill"
                      }
                    ],
                    "interval": "",
                    "intervalFactor": 2,
                    "legendFormat": "rqps",
                    "orderByTime": "ASC",
                    "policy": "default",
                    "refId": "A",
                    "resultFormat": "time_series",
                    "select": [
                      [
                        {
                          "params": [
                            "value"
                          ],
                          "type": "field"
                        },
                        {
                          "params": [],
                          "type": "mean"
                        }
                      ]
                    ],
                    "step": 60,
                    "tags": []
                  }
                ],
                "thresholds": "30,60",
                "timeFrom": null,
                "timeShift": null,
                "title": "Trafego Médio",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "N/A",
                    "value": "null"
                  }
                ],
                "valueName": "current"
              },
              {
                "cacheTimeout": null,
                "colorBackground": false,
                "colorValue": false,
                "colors": [
                  "rgba(50, 172, 45, 0.97)",
                  "rgba(237, 129, 40, 0.89)",
                  "rgba(245, 54, 54, 0.9)"
                ],
                "datasource": "Prometheus-App",
                "description": "Tráfego total nos pods",
                "format": "reqps",
                "gauge": {
                  "maxValue": 900,
                  "minValue": 0,
                  "show": true,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 4,
                  "w": 6,
                  "x": 18,
                  "y": 1
                },
                "hideTimeOverride": false,
                "id": 17,
                "interval": null,
                "links": [],
                "mappingType": 1,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "N/A",
                    "to": "null"
                  }
                ],
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "rgb(31, 120, 193)",
                  "show": false
                },
                "tableColumn": "",
                "targets": [
                  {
                    "dsType": "influxdb",
                    "expr": "avg(rate(http_requests_seconds_summary_count{namespace=~\"$Namespace\"}[$timerange]))",
                    "format": "time_series",
                    "groupBy": [
                      {
                        "params": [
                          "$__interval"
                        ],
                        "type": "time"
                      },
                      {
                        "params": [
                          "null"
                        ],
                        "type": "fill"
                      }
                    ],
                    "interval": "",
                    "intervalFactor": 2,
                    "legendFormat": "",
                    "orderByTime": "ASC",
                    "policy": "default",
                    "refId": "A",
                    "resultFormat": "time_series",
                    "select": [
                      [
                        {
                          "params": [
                            "value"
                          ],
                          "type": "field"
                        },
                        {
                          "params": [],
                          "type": "mean"
                        }
                      ]
                    ],
                    "step": 60,
                    "tags": []
                  }
                ],
                "thresholds": "300,600",
                "timeFrom": null,
                "timeShift": null,
                "title": "Trafego Total",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "N/A",
                    "value": "null"
                  }
                ],
                "valueName": "current"
              },
              {
                "cacheTimeout": null,
                "colorBackground": true,
                "colorValue": false,
                "colors": [
                  "#73BF69",
                  "#1F60C4",
                  "#d44a3a"
                ],
                "datasource": "Prometheus-App",
                "format": "none",
                "gauge": {
                  "maxValue": 100,
                  "minValue": 0,
                  "show": false,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 4,
                  "w": 3,
                  "x": 0,
                  "y": 5
                },
                "id": 15,
                "interval": null,
                "links": [],
                "mappingType": 1,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "N/A",
                    "to": "null"
                  }
                ],
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "rgb(31, 120, 193)",
                  "show": false
                },
                "tableColumn": "",
                "targets": [
                  {
                    "expr": "sum(up{namespace=~\"$Namespace\"})",
                    "format": "time_series",
                    "hide": false,
                    "interval": "",
                    "intervalFactor": 1,
                    "legendFormat": "",
                    "refId": "A"
                  }
                ],
                "thresholds": "",
                "timeFrom": null,
                "timeShift": null,
                "title": "Pods",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "N/A",
                    "value": "null"
                  }
                ],
                "valueName": "current"
              },
              {
                "cacheTimeout": null,
                "colorBackground": true,
                "colorValue": false,
                "colors": [
                  "#73BF69",
                  "rgba(237, 129, 40, 0.89)",
                  "#d44a3a"
                ],
                "datasource": "Prometheus-App",
                "format": "none",
                "gauge": {
                  "maxValue": 100,
                  "minValue": 0,
                  "show": false,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 4,
                  "w": 4,
                  "x": 3,
                  "y": 5
                },
                "id": 10,
                "interval": null,
                "links": [],
                "mappingType": 1,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "N/A",
                    "to": "null"
                  }
                ],
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "rgb(31, 120, 193)",
                  "show": false
                },
                "tableColumn": "Value",
                "targets": [
                  {
                    "expr": "sum(delta(http_requests_seconds_summary_count{namespace=~"$Namespace"}[$timerange]))",
                    "format": "time_series",
                    "hide": false,
                    "instant": true,
                    "interval": "",
                    "intervalFactor": 1,
                    "legendFormat": "",
                    "refId": "A"
                  }
                ],
                "thresholds": "",
                "timeFrom": null,
                "timeShift": null,
                "title": "Requisições",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "-",
                    "value": "null"
                  }
                ],
                "valueName": "current"
              },
              {
                "cacheTimeout": null,
                "colorBackground": false,
                "colorValue": false,
                "colors": [
                  "#299c46",
                  "rgba(237, 129, 40, 0.89)",
                  "#C4162A"
                ],
                "datasource": "Prometheus-App",
                "decimals": 0,
                "format": "none",
                "gauge": {
                  "maxValue": 100,
                  "minValue": 0,
                  "show": false,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 4,
                  "w": 3,
                  "x": 7,
                  "y": 5
                },
                "id": 8,
                "interval": null,
                "links": [],
                "mappingType": 1,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "pluginVersion": "6.1.0",
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "N/A",
                    "to": "null"
                  }
                ],
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "rgb(31, 120, 193)",
                  "show": false
                },
                "tableColumn": "",
                "targets": [
                  {
                    "expr": "sum(delta(http_requests_seconds_summary_count{namespace=~"$Namespace",status!="200"}[$timerange]))",
                    "format": "time_series",
                    "instant": true,
                    "interval": "",
                    "intervalFactor": 1,
                    "legendFormat": "",
                    "refId": "A"
                  }
                ],
                "thresholds": "",
                "timeFrom": null,
                "timeShift": null,
                "title": "Erros",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "0",
                    "value": "null"
                  }
                ],
                "valueName": "current"
              },
              {
                "cacheTimeout": null,
                "colorBackground": false,
                "colorValue": false,
                "colors": [
                  "#299c46",
                  "rgba(237, 129, 40, 0.89)",
                  "#d44a3a"
                ],
                "decimals": 2,
                "format": "percent",
                "gauge": {
                  "maxValue": 100,
                  "minValue": 0,
                  "show": false,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 4,
                  "w": 2,
                  "x": 10,
                  "y": 5
                },
                "id": 26,
                "interval": "",
                "links": [],
                "mappingType": 1,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "N/A",
                    "to": "null"
                  }
                ],
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "rgb(31, 120, 193)",
                  "show": false
                },
                "tableColumn": "",
                "targets": [
                  {
                    "expr": "sum(delta(http_requests_seconds_summary_count{namespace=~"$Namespace",status!="200"}[$timerange]))/sum(delta(http_requests_seconds_summary_count{namespace=~"$Namespace"}[$timerange]))*100",
                    "format": "time_series",
                    "intervalFactor": 1,
                    "legendFormat": "",
                    "refId": "A"
                  }
                ],
                "thresholds": "",
                "title": "Erros %",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "0 %",
                    "value": "null"
                  }
                ],
                "valueName": "current"
              },
              {
                "cacheTimeout": null,
                "colorBackground": false,
                "colorValue": false,
                "colors": [
                  "rgba(50, 172, 45, 0.97)",
                  "rgba(237, 129, 40, 0.89)",
                  "rgba(245, 54, 54, 0.9)"
                ],
                "description": "Latência média das requisições nos pods",
                "format": "s",
                "gauge": {
                  "maxValue": 0.1,
                  "minValue": 0,
                  "show": true,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 4,
                  "w": 6,
                  "x": 12,
                  "y": 5
                },
                "id": 18,
                "interval": null,
                "links": [],
                "mappingType": 1,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "N/A",
                    "to": "null"
                  }
                ],
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "rgb(31, 120, 193)",
                  "show": false
                },
                "tableColumn": "",
                "targets": [
                  {
                    "dsType": "influxdb",
                    "expr": "avg(http_requests_seconds_summary_sum{{namespace=~"$Namespace"}}/http_requests_seconds_summary_count{{namespace=~"$Namespace"}})",
                    "format": "time_series",
                    "groupBy": [
                      {
                        "params": [
                          "$__interval"
                        ],
                        "type": "time"
                      },
                      {
                        "params": [
                          "null"
                        ],
                        "type": "fill"
                      }
                    ],
                    "hide": false,
                    "intervalFactor": 2,
                    "legendFormat": "",
                    "orderByTime": "ASC",
                    "policy": "default",
                    "refId": "A",
                    "resultFormat": "time_series",
                    "select": [
                      [
                        {
                          "params": [
                            "value"
                          ],
                          "type": "field"
                        },
                        {
                          "params": [],
                          "type": "mean"
                        }
                      ]
                    ],
                    "step": 60,
                    "tags": []
                  }
                ],
                "thresholds": "0.02,0.05",
                "title": "Latência Média",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "0",
                    "value": "0"
                  }
                ],
                "valueName": "current"
              },
              {
                "cacheTimeout": null,
                "colorBackground": false,
                "colorValue": false,
                "colors": [
                  "rgba(50, 172, 45, 0.97)",
                  "rgba(237, 129, 40, 0.89)",
                  "rgba(245, 54, 54, 0.9)"
                ],
                "description": "Máximo consumo de memória no período",
                "format": "percent",
                "gauge": {
                  "maxValue": 100,
                  "minValue": 0,
                  "show": true,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 4,
                  "w": 6,
                  "x": 18,
                  "y": 5
                },
                "id": 20,
                "interval": null,
                "links": [],
                "mappingType": 1,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "N/A",
                    "to": "null"
                  }
                ],
                "repeat": null,
                "repeatDirection": "v",
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "rgb(31, 120, 193)",
                  "show": false
                },
                "tableColumn": "",
                "targets": [
                  {
                    "dsType": "influxdb",
                    "expr": "max(max_over_time(dev_typescript_nodejs_heap_size_used_bytes{namespace=~"$Namespace"}[$timerange]))/max(max_over_time(dev_typescript_nodejs_heap_size_total_bytes{namespace=~"$Namespace"}[$timerange]))*100",
                    "format": "time_series",
                    "groupBy": [
                      {
                        "params": [
                          "$__interval"
                        ],
                        "type": "time"
                      },
                      {
                        "params": [
                          "null"
                        ],
                        "type": "fill"
                      }
                    ],
                    "hide": false,
                    "intervalFactor": 2,
                    "legendFormat": "",
                    "orderByTime": "ASC",
                    "policy": "default",
                    "refId": "A",
                    "resultFormat": "time_series",
                    "select": [
                      [
                        {
                          "params": [
                            "value"
                          ],
                          "type": "field"
                        },
                        {
                          "params": [],
                          "type": "mean"
                        }
                      ]
                    ],
                    "step": 60,
                    "tags": []
                  }
                ],
                "thresholds": "50,80",
                "title": "Memória Máxima",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "N/A",
                    "value": "null"
                  }
                ],
                "valueName": "current"
              },
              {
                "cacheTimeout": null,
                "colorBackground": false,
                "colorValue": false,
                "colors": [
                  "#299c46",
                  "rgba(237, 129, 40, 0.89)",
                  "#d44a3a"
                ],
                "datasource": "Prometheus-App",
                "description": "",
                "format": "none",
                "gauge": {
                  "maxValue": 100,
                  "minValue": 0,
                  "show": false,
                  "thresholdLabels": false,
                  "thresholdMarkers": true
                },
                "gridPos": {
                  "h": 2,
                  "w": 24,
                  "x": 0,
                  "y": 9
                },
                "id": 42,
                "interval": null,
                "links": [],
                "mappingType": 2,
                "mappingTypes": [
                  {
                    "name": "value to text",
                    "value": 1
                  },
                  {
                    "name": "range to text",
                    "value": 2
                  }
                ],
                "maxDataPoints": 100,
                "maxPerRow": 4,
                "nullPointMode": "connected",
                "nullText": null,
                "options": {},
                "postfix": "",
                "postfixFontSize": "50%",
                "prefix": "",
                "prefixFontSize": "50%",
                "rangeMaps": [
                  {
                    "from": "null",
                    "text": "N/A",
                    "to": "null"
                  }
                ],
                "repeat": "Namespace",
                "repeatDirection": "h",
                "scopedVars": {
                  "Namespace": {
                    "isNone": true,
                    "selected": true,
                    "text": "None",
                    "value": ""
                  }
                },
                "sparkline": {
                  "fillColor": "rgba(31, 118, 189, 0.18)",
                  "full": false,
                  "lineColor": "rgb(31, 120, 193)",
                  "show": false
                },
                "tableColumn": "",
                "targets": [
                  {
                    "expr": "topk(1, sort_desc(base_cpu_availableProcessors{namespace=~\"$Namespace\"}))",
                    "format": "time_series",
                    "instant": false,
                    "interval": "",
                    "intervalFactor": 10,
                    "legendFormat": "{{serviceVersion}}",
                    "refId": "A"
                  }
                ],
                "thresholds": "",
                "timeFrom": null,
                "timeShift": null,
                "title": "Versão Aplicação - $Namespace",
                "type": "singlestat",
                "valueFontSize": "80%",
                "valueMaps": [
                  {
                    "op": "=",
                    "text": "N/A",
                    "value": "null"
                  }
                ],
                "valueName": "name"
              },
              {
                "collapsed": false,
                "gridPos": {
                  "h": 1,
                  "w": 24,
                  "x": 0,
                  "y": 11
                },
                "id": 31,
                "panels": [],
                "title": "Gráficos",
                "type": "row"
              },
              {
                "aliasColors": {},
                "bars": false,
                "dashLength": 10,
                "dashes": false,
                "datasource": "Prometheus-App",
                "fill": 1,
                "fillGradient": 0,
                "gridPos": {
                  "h": 6,
                  "w": 12,
                  "x": 0,
                  "y": 18
                },
                "id": 40,
                "legend": {
                  "avg": false,
                  "current": false,
                  "max": false,
                  "min": false,
                  "show": false,
                  "total": false,
                  "values": false
                },
                "lines": true,
                "linewidth": 1,
                "links": [],
                "nullPointMode": "null",
                "options": {
                  "dataLinks": []
                },
                "paceLength": 10,
                "percentage": false,
                "pointradius": 2,
                "points": false,
                "renderer": "flot",
                "seriesOverrides": [],
                "spaceLength": 10,
                "stack": false,
                "steppedLine": false,
                "targets": [
                  {
                    "expr": "http_requests_seconds_summary_sum{{namespace=~\"$Namespace\"}}/http_requests_seconds_summary_count{{namespace=~\"$Namespace\"}}",
                    "format": "time_series",
                    "hide": false,
                    "instant": false,
                    "intervalFactor": 1,
                    "legendFormat": "{{pod}}",
                    "refId": "A"
                  }
                ],
                "thresholds": [],
                "timeFrom": null,
                "timeRegions": [],
                "timeShift": null,
                "title": "Latência Aplicação",
                "tooltip": {
                  "shared": true,
                  "sort": 0,
                  "value_type": "individual"
                },
                "type": "graph",
                "xaxis": {
                  "buckets": null,
                  "mode": "time",
                  "name": null,
                  "show": true,
                  "values": []
                },
                "yaxes": [
                  {
                    "format": "s",
                    "label": null,
                    "logBase": 1,
                    "max": null,
                    "min": "0",
                    "show": true
                  },
                  {
                    "format": "short",
                    "label": null,
                    "logBase": 1,
                    "max": null,
                    "min": null,
                    "show": true
                  }
                ],
                "yaxis": {
                  "align": false,
                  "alignLevel": null
                }
              },
              {
                "aliasColors": {},
                "bars": false,
                "dashLength": 10,
                "dashes": false,
                "datasource": "Prometheus-App",
                "description": "",
                "fill": 0,
                "fillGradient": 0,
                "gridPos": {
                  "h": 6,
                  "w": 12,
                  "x": 12,
                  "y": 18
                },
                "id": 38,
                "legend": {
                  "avg": false,
                  "current": false,
                  "max": false,
                  "min": false,
                  "show": false,
                  "total": false,
                  "values": false
                },
                "lines": true,
                "linewidth": 1,
                "links": [],
                "nullPointMode": "null",
                "options": {
                  "dataLinks": []
                },
                "paceLength": 10,
                "percentage": false,
                "pointradius": 2,
                "points": false,
                "renderer": "flot",
                "seriesOverrides": [],
                "spaceLength": 10,
                "stack": false,
                "steppedLine": false,
                "targets": [
                  {
                    "expr": "sum(up{namespace=~\"$Namespace\"})",
                    "format": "time_series",
                    "instant": false,
                    "intervalFactor": 10,
                    "legendFormat": "{{pod}}",
                    "refId": "A"
                  }
                ],
                "thresholds": [],
                "timeFrom": null,
                "timeRegions": [],
                "timeShift": null,
                "title": "Quantidade de Pods Ativo",
                "tooltip": {
                  "shared": true,
                  "sort": 0,
                  "value_type": "individual"
                },
                "type": "graph",
                "xaxis": {
                  "buckets": null,
                  "mode": "time",
                  "name": null,
                  "show": true,
                  "values": []
                },
                "yaxes": [
                  {
                    "decimals": 0,
                    "format": "short",
                    "label": "",
                    "logBase": 1,
                    "max": null,
                    "min": null,
                    "show": true
                  },
                  {
                    "format": "short",
                    "label": null,
                    "logBase": 1,
                    "max": null,
                    "min": null,
                    "show": true
                  }
                ],
                "yaxis": {
                  "align": false,
                  "alignLevel": null
                }
              },
              {
                "aliasColors": {},
                "bars": false,
                "dashLength": 10,
                "dashes": false,
                "fill": 1,
                "fillGradient": 0,
                "gridPos": {
                  "h": 9,
                  "w": 12,
                  "x": 0,
                  "y": 24
                },
                "id": 36,
                "legend": {
                  "avg": false,
                  "current": false,
                  "max": false,
                  "min": false,
                  "show": true,
                  "total": false,
                  "values": false
                },
                "lines": true,
                "linewidth": 1,
                "links": [],
                "nullPointMode": "null",
                "options": {
                  "dataLinks": []
                },
                "paceLength": 10,
                "percentage": false,
                "pointradius": 2,
                "points": false,
                "renderer": "flot",
                "seriesOverrides": [],
                "spaceLength": 10,
                "stack": false,
                "steppedLine": false,
                "targets": [
                  {
                    "expr": "avg(http_requests_seconds_summary_count{pod=~\"$Pod\"}) by (quantile)",
                    "format": "time_series",
                    "hide": false,
                    "instant": false,
                    "intervalFactor": 1,
                    "legendFormat": "{{quantile}}",
                    "refId": "A"
                  }
                ],
                "thresholds": [],
                "timeFrom": null,
                "timeRegions": [],
                "timeShift": null,
                "title": "Latency Percentile",
                "tooltip": {
                  "shared": true,
                  "sort": 0,
                  "value_type": "individual"
                },
                "type": "graph",
                "xaxis": {
                  "buckets": null,
                  "mode": "time",
                  "name": null,
                  "show": true,
                  "values": []
                },
                "yaxes": [
                  {
                    "format": "s",
                    "label": null,
                    "logBase": 1,
                    "max": null,
                    "min": "0",
                    "show": true
                  },
                  {
                    "format": "short",
                    "label": null,
                    "logBase": 1,
                    "max": null,
                    "min": null,
                    "show": true
                  }
                ],
                "yaxis": {
                  "align": false,
                  "alignLevel": null
                }
              },
              {
                "aliasColors": {},
                "bars": false,
                "dashLength": 10,
                "dashes": false,
                "datasource": "Prometheus-App",
                "fill": 0,
                "fillGradient": 0,
                "gridPos": {
                  "h": 9,
                  "w": 12,
                  "x": 12,
                  "y": 24
                },
                "id": 23,
                "interval": "",
                "legend": {
                  "avg": false,
                  "current": false,
                  "max": false,
                  "min": false,
                  "show": false,
                  "total": false,
                  "values": false
                },
                "lines": true,
                "linewidth": 1,
                "links": [],
                "nullPointMode": "null",
                "options": {
                  "dataLinks": []
                },
                "paceLength": 10,
                "percentage": false,
                "pointradius": 2,
                "points": false,
                "renderer": "flot",
                "seriesOverrides": [],
                "spaceLength": 10,
                "stack": false,
                "steppedLine": false,
                "targets": [
                  {
                    "expr": "dev_typescript_nodejs_heap_size_used_bytes{namespace=~\"$Namespace\",pod=~\"$Pod\"}",
                    "format": "time_series",
                    "instant": false,
                    "intervalFactor": 1,
                    "legendFormat": "{{pod}}",
                    "refId": "A"
                  },
                  {
                    "expr": "dev_typescript_nodejs_heap_size_total_bytes{namespace=~\"$Namespace\",pod=~\"$Pod\",endpoint=\"app\"}",
                    "format": "time_series",
                    "hide": false,
                    "intervalFactor": 1,
                    "legendFormat": "{{pod}}",
                    "refId": "B"
                  }
                ],
                "thresholds": [],
                "timeFrom": null,
                "timeRegions": [],
                "timeShift": null,
                "title": "Memory - Used Heap x MaxHeap",
                "tooltip": {
                  "shared": true,
                  "sort": 0,
                  "value_type": "individual"
                },
                "type": "graph",
                "xaxis": {
                  "buckets": null,
                  "mode": "time",
                  "name": null,
                  "show": true,
                  "values": []
                },
                "yaxes": [
                  {
                    "format": "decbytes",
                    "label": null,
                    "logBase": 1,
                    "max": null,
                    "min": null,
                    "show": true
                  },
                  {
                    "format": "short",
                    "label": null,
                    "logBase": 1,
                    "max": null,
                    "min": null,
                    "show": true
                  }
                ],
                "yaxis": {
                  "align": false,
                  "alignLevel": null
                }
              }
            ],
            "refresh": "5s",
            "schemaVersion": 19,
            "style": "dark",
            "tags": [],
            "templating": {
              "list": [
                {
                  "allValue": "",
                  "current": {
                    "text": "prometheus app",
                    "value": "prometheus app"
                  },
                  "datasource": "Prometheus-App",
                  "definition": "label_values(up, namespace)",
                  "hide": 0,
                  "includeAll": false,
                  "label": "Namespace",
                  "multi": true,
                  "name": "Namespace",
                  "options": [],
                  "query": "label_values(up, namespace)",
                  "refresh": 2,
                  "regex": "",
                  "skipUrlSync": false,
                  "sort": 0,
                  "tagValuesQuery": "",
                  "tags": [],
                  "tagsQuery": "",
                  "type": "query",
                  "useTags": false
                },
                {
                  "allValue": "",
                  "current": {
                    "text": "All",
                    "value": [
                      "$__all"
                    ]
                  },
                  "datasource": "Prometheus-App",
                  "definition": "label_values(up{namespace=\"$Namespace\"}, pod)",
                  "hide": 0,
                  "includeAll": true,
                  "label": "Pod",
                  "multi": true,
                  "name": "Pod",
                  "options": [],
                  "query": "label_values(up{namespace=\"$Namespace\"}, pod)",
                  "refresh": 2,
                  "regex": "",
                  "skipUrlSync": false,
                  "sort": 0,
                  "tagValuesQuery": "",
                  "tags": [],
                  "tagsQuery": "",
                  "type": "query",
                  "useTags": false
                },
                {
                  "auto": true,
                  "auto_count": 1,
                  "auto_min": "10s",
                  "current": {
                    "text": "auto",
                    "value": "$__auto_interval_timerange"
                  },
                  "hide": 2,
                  "label": null,
                  "name": "timerange",
                  "options": [
                    {
                      "selected": true,
                      "text": "auto",
                      "value": "$__auto_interval_timerange"
                    },
                    {
                      "selected": false,
                      "text": "1m",
                      "value": "1m"
                    },
                    {
                      "selected": false,
                      "text": "10m",
                      "value": "10m"
                    },
                    {
                      "selected": false,
                      "text": "30m",
                      "value": "30m"
                    },
                    {
                      "selected": false,
                      "text": "1h",
                      "value": "1h"
                    },
                    {
                      "selected": false,
                      "text": "6h",
                      "value": "6h"
                    },
                    {
                      "selected": false,
                      "text": "12h",
                      "value": "12h"
                    },
                    {
                      "selected": false,
                      "text": "1d",
                      "value": "1d"
                    },
                    {
                      "selected": false,
                      "text": "7d",
                      "value": "7d"
                    },
                    {
                      "selected": false,
                      "text": "14d",
                      "value": "14d"
                    },
                    {
                      "selected": false,
                      "text": "30d",
                      "value": "30d"
                    }
                  ],
                  "query": "1m,10m,30m,1h,6h,12h,1d,7d,14d,30d",
                  "refresh": 2,
                  "skipUrlSync": false,
                  "type": "interval"
                }
              ]
            },
            "time": {
              "from": "now-30m",
              "to": "now"
            },
            "timepicker": {
              "refresh_intervals": [
                "5s",
                "10s",
                "30s",
                "1m",
                "5m",
                "15m",
                "30m",
                "1h",
                "2h",
                "1d"
              ],
              "time_options": [
                "5m",
                "15m",
                "1h",
                "6h",
                "12h",
                "24h",
                "2d",
                "7d",
                "30d"
              ]
            },
            "timezone": "",
            "title": "Four Golden Signals",
            "uid": "ODDQPkiZy",
            "version": 1
          }  
      namespaces-monitorados:
        json: |-
          {
            "annotations": {
              "list": [
                {
                  "builtIn": 1,
                  "datasource": "-- Grafana --",
                  "enable": true,
                  "hide": true,
                  "iconColor": "rgba(0, 211, 255, 1)",
                  "name": "Annotations & Alerts",
                  "type": "dashboard"
                }
              ]
            },
            "editable": true,
            "gnetId": null,
            "graphTooltip": 0,
            "id": 1,
            "links": [],
            "panels": [
              {
                "aliasColors": {},
                "bars": false,
                "columns": [],
                "dashLength": 10,
                "dashes": false,
                "datasource": "Prometheus-App",
                "fill": 1,
                "fontSize": "100%",
                "gridPos": {
                  "h": 13,
                  "w": 24,
                  "x": 0,
                  "y": 0
                },
                "id": 2,
                "legend": {
                  "avg": false,
                  "current": false,
                  "max": false,
                  "min": false,
                  "show": true,
                  "total": false,
                  "values": false
                },
                "lines": true,
                "linewidth": 1,
                "links": [],
                "nullPointMode": "null as zero",
                "options": {},
                "pageSize": null,
                "percentage": false,
                "pointradius": 5,
                "points": false,
                "renderer": "flot",
                "scroll": true,
                "seriesOverrides": [],
                "showHeader": true,
                "sort": {
                  "col": 2,
                  "desc": true
                },
                "spaceLength": 10,
                "stack": false,
                "steppedLine": false,
                "styles": [
                  {
                    "alias": "",
                    "colorMode": null,
                    "colors": [
                      "rgba(245, 54, 54, 0.9)",
                      "rgba(237, 129, 40, 0.89)",
                      "rgba(50, 172, 45, 0.97)"
                    ],
                    "dateFormat": "YYYY-MM-DD HH:mm:ss",
                    "decimals": 2,
                    "mappingType": 1,
                    "pattern": "Time",
                    "thresholds": [],
                    "type": "hidden",
                    "unit": "short"
                  },
                  {
                    "alias": "Namespace",
                    "colorMode": null,
                    "colors": [
                      "rgba(245, 54, 54, 0.9)",
                      "rgba(237, 129, 40, 0.89)",
                      "rgba(50, 172, 45, 0.97)"
                    ],
                    "dateFormat": "YYYY-MM-DD HH:mm:ss",
                    "decimals": 2,
                    "link": true,
                    "linkTooltip": "Four golden Signs do ${__cell}",
                    "linkUrl": "d/ODDQPkiZy/four-golden-signals?orgId=1&refresh=5s&from=now-5m&to=now&var-Namespace=${__cell}&var-Pod=All&var-timerange=$__auto_interval_timerange",
                    "mappingType": 1,
                    "pattern": "namespace",
                    "preserveFormat": false,
                    "sanitize": false,
                    "thresholds": [],
                    "type": "string",
                    "unit": "short"
                  },
                  {
                    "alias": "Containers",
                    "colorMode": null,
                    "colors": [
                      "rgba(245, 54, 54, 0.9)",
                      "rgba(237, 129, 40, 0.89)",
                      "rgba(50, 172, 45, 0.97)"
                    ],
                    "dateFormat": "YYYY-MM-DD HH:mm:ss",
                    "decimals": 0,
                    "link": false,
                    "mappingType": 1,
                    "pattern": "Value",
                    "thresholds": [],
                    "type": "number",
                    "unit": "short"
                  }
                ],
                "targets": [
                  {
                    "expr": "count by(namespace) (up{job=~\".*-f.*\"})",
                    "format": "table",
                    "instant": true,
                    "intervalFactor": 2,
                    "legendFormat": "{{ namespace }}",
                    "refId": "A",
                    "step": 10
                  }
                ],
                "thresholds": [],
                "timeFrom": null,
                "timeShift": null,
                "title": "Namespaces Monitorados",
                "tooltip": {
                  "shared": false,
                  "sort": 0,
                  "value_type": "individual"
                },
                "transform": "table",
                "type": "table",
                "xaxis": {
                  "buckets": null,
                  "mode": "time",
                  "name": null,
                  "show": true,
                  "values": []
                },
                "yaxes": [
                  {
                    "format": "short",
                    "label": null,
                    "logBase": 1,
                    "max": null,
                    "min": 0,
                    "show": true
                  },
                  {
                    "format": "short",
                    "label": null,
                    "logBase": 1,
                    "max": null,
                    "min": null,
                    "show": false
                  }
                ]
              }
            ],
            "schemaVersion": 19,
            "style": "dark",
            "tags": [],
            "templating": {
              "list": []
            },
            "time": {
              "from": "now-6h",
              "to": "now"
            },
            "timepicker": {
              "refresh_intervals": [
                "5s",
                "10s",
                "30s",
                "1m",
                "5m",
                "15m",
                "30m",
                "1h",
                "2h",
                "1d"
              ]
            },
            "timezone": "",
            "title": "Namespaces Monitorados",
            "uid": "rJjqQYJZz",
            "version": 1
          }
        

  persistence:
    enabled: true
    storageClassName: nas-client
    accessModes:
      - ReadWriteOnce
    size: 1Gi
    # annotations: {}
    # subPath: ""
    # existingClaim:

  ldapBB:
    enabled: true



dnsservice:
  urls: 
  # Padrão para desenvolvimento
  # subdominio . sigla . desenv.bb.com.br  
      - monitor.<sigla>.desenv.bb.com.br
      - prometheus.<sigla>.desenv.bb.com.br     
      - alertmanager.<sigla>.desenv.bb.com.br 

```---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/values_typescript.md&internalidade=monitoracao/values_typescript)
