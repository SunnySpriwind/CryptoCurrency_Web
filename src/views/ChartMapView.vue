<template>
  <div class="chart-map-view">

    <el-button
        class="back-button"
        @click="navigateTo('/main')"
        type="primary"
    >
      Back
    </el-button>

    <div class="right-buttons">
      <el-button
          class="how-to-use-button"
          @click="showHowToUse"
          type="info"
      >
        How to Use
      </el-button>
      <el-button
          class="save-button"
          @click="saveData"
          type="success"
      >
        Save
      </el-button>
    </div>

    <el-dialog
        title="How to Use"
        :visible.sync="howToUseVisible"
        width="60%"
    >
      <div>
        <h2>About Chart Map</h2>
        <p>This website is Chart Map, You can show any chart that is provided you want to view. Enter the name of
          cryptocurrency and select market.</p>

        <h2> Attention</h2>
        <p>Because the restriction of CoinMarketCap API, The K line is not provided to CoinMarketCap. Thus, you can
          not select K line chart when you select CoinMarketcap </p>

        <p>Because the scale of data, the page may be not responding. Thus, the functionality of real-time update is
          close.</p>>

        <h2>What is K line</h2>
        <p>The K-line, also known as a candlestick chart, is a popular financial chart used to describe price movements
          of a security, derivative, or currency over a specific period of time. Each "candlestick" on the chart
          represents the price action during a particular time frame (such as one day, one hour, etc.), and consists of
          a body and two shadows (also known as wicks).</p>

        <p>The Body: The body of the candlestick represents the opening and closing prices of the asset during that time
          frame. If the closing price is higher than the opening price, the body is typically colored (often green or
          white) to indicate a rise in price. If the closing price is lower than the opening price, the body is colored
          differently (often red or black) to show a decline in price.</p>

        <p>The Shadows (Wicks): The lines extending above and below the body are called shadows or wicks. The upper
          shadow indicates the highest price during the period, while the lower shadow shows the lowest price.</p>

        <p>K-lines are widely used by traders and investors to identify trends, potential reversals, and to make
          informed trading decisions based on the visual patterns they create over time. The simplicity and
          effectiveness of K-lines make them a fundamental tool in technical analysis.</p>

        <h2>Real-time Price Chart, Volumn and Marketcap</h2>
        <p>Real-time Price Chart, Volumn and Marketcap are important metrics for cryptocurrency.</p>
        <p>Real-time Price: How many USD equals one cryptocurrency</p>
        <p>Volumn: Volume refers to the total amount of a cryptocurrency that has been traded within a specific time
          period, usually within 24 hours.</p>
        <p>Market Cap: Market capitalization is the total value of all the coins or tokens of a cryptocurrency that are
          currently in circulation. It is calculated by multiplying the current price of the cryptocurrency by its total
          circulating supply.</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="howToUseVisible = false">Close</el-button>
      </span>

    </el-dialog>

    <el-form :inline="true" label-position="left" label-width="50px" class="top-controls">
      <el-form-item label="Coin: ">
        <el-autocomplete
            v-model="currencyName"
            :fetch-suggestions="querySearch"
            placeholder="Enter currency name"
            class="custom-input"
        ></el-autocomplete>
      </el-form-item>

      <el-form-item label="Market: ">
        <el-select v-model="marketName" placeholder="Select market" class="custom-select">
          <el-option label="CoinGecko" value="CoinGecko"></el-option>
          <el-option label="CoinMarketCap" value="CoinMarketCap"></el-option>
        </el-select>
      </el-form-item>
    </el-form>


    <div class="floating-buttons">
      <div v-for="chart in availableCharts" :key="chart" class="chart-control">
        <el-button
            :type="isChartSelected(chart) ? 'primary' : 'default'"
            @click="toggleChart(chart)"
            class="custom-button"
        >
          {{ chart }}
        </el-button>

        <el-button
            v-if="isChartSelected(chart) && chart === 'Kline'"
            @click="handleKlineCommand('1day')"
            class="custom-button secondary"
        >
          1Day
        </el-button>
        <el-button
            v-if="isChartSelected(chart) && chart === 'Kline'"
            @click="handleKlineCommand('7day')"
            class="custom-button secondary"
        >
          7Day
        </el-button>
        <el-button
            v-if="isChartSelected(chart) && chart === 'Kline'"
            @click="handleKlineCommand('1month')"
            class="custom-button secondary"
        >
          1Month
        </el-button>

        <el-button
            v-if="isChartSelected(chart) && chart !== 'Kline'"
            @click="toggleChartType(chart)"
            class="custom-button secondary change-chart"
        >
          Change to {{ chartTypes[chart] === 'line' ? 'Bar Chart' : 'Line Chart' }}
        </el-button>
      </div>
    </div>

    <div class="charts-container">
      <div
          v-for="chart in selectedCharts"
          :key="chart"
          :class="chartClass"
      >
        <div :id="chart" :style="chartStyle" class="chart"></div>
      </div>
    </div>
    <el-dialog
        title="Chart View"
        :visible.sync="dialogVisible"
        width="70%"
        :before-close="handleClose"
    >
      <div style="position: relative;">
        <!-- Chart Container -->
        <div :id="'dialog-chart'" :style="{ width: '100%', height: '500px' }"></div>
      </div>
    </el-dialog>
  </div>


</template>

<style>
body {
  background: linear-gradient(135deg, #f5f7fa, #c3cfe2);
  font-family: 'Roboto', sans-serif;
}

.chart-map-view {
  padding: 20px;
  max-width: 1200px;
  margin: auto;
  position: relative;
}

.top-controls {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.custom-input,
.custom-select {
  width: 250px;
  border-radius: 25px;
  padding: 10px 15px;
  margin-top: -10px;
}

.custom-button {
  border-radius: 25px;
  padding: 10px 20px;
  font-weight: bold;
  transition: background-color 0.3s, transform 0.3s;
}

.custom-button.primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.custom-button.secondary {
  background: linear-gradient(135deg, #f6d365, #fda085);
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.chart-control:hover .secondary {
  opacity: 1;
}

.custom-button:hover {
  transform: translateY(-3px);
}

.chart-control {
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
}


.floating-buttons {
  position: fixed;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 15px;
  z-index: 1000;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-top: 20px;
  width: 100%;
  justify-items: center;
}

.chart {
  border-radius: 4px;
  background-color: white;
}

.chart.single {
  width: 100%;
  height: 500px;
}

.chart.two,
.chart.multiple {
  width: 100%;
  height: 400px;
}

.back-button {
  position: absolute;
  top: 20px;
  left: 20px;
}

.right-buttons {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
}

.right-buttons .how-to-use-button,
.right-buttons .save-button {
  margin: 0;
}

</style>

<script>
import request from '@/utils/request';
import * as echarts from 'echarts';
import {jwtDecode} from 'jwt-decode';

export default {
  name: 'ChartMapView',
  data() {
    return {
      currencyName: '',
      marketName: '',
      availableCharts: ['Kline', 'Realtime', 'Volumn', 'Marketcap'],
      availableCurrencies: ['bitcoin', 'ethereum', 'binancecoin'],
      selectedCharts: [],
      klineData: [],
      period: '30min',
      realtimeData: [],
      volumnData: [],
      marketcapData: [],
      exchangeData: [],
      errorMessage: '',
      chartTypes: {
        Realtime: 'line',
        Volumn: 'line',
        Marketcap: 'line',
        Exchange: 'line',
      },
      timers: {},
      howToUseVisible: false,
      dialogVisible: false,
      selectedChartData: null,
    };
  },
  computed: {
    // When different charts are selected, rerender charts.
    chartClass() {
      const chartCount = this.selectedCharts.length;
      if (chartCount === 1) {
        this.clearAndRerenderCharts();
        // return the name of css
        return 'chart single';
      } else if (chartCount >= 2) {
        this.clearAndRerenderCharts();
        // return the name of css
        return 'chart multiple';
      }
      return '';
    },
    chartStyle() {
      const chartCount = this.selectedCharts.length;
      let style = {};
      if (chartCount === 1) {
        // define the css attributes
        style = {width: '100%', height: '500px'};
      } else if (chartCount === 2 || chartCount > 2) {
        // define the css attributes
        style = {width: 'calc(100%)', height: '400px'};
      }
      return style;
    },
  },

  watch: {
    selectedCharts(newVal) {
      const shouldStartTimer = newVal.includes('Realtime') || newVal.includes('Volumn') || newVal.includes('Marketcap');

      if (shouldStartTimer && !this.timers['update']) {
        this.setupTimer(); // Start timer if needed
      } else if (!shouldStartTimer && this.timers['update']) {
        this.clearTimer(); // Clear timer if not needed
      }
    },
  },

  mounted() {
    // To get params from URls.
    const query = this.$route.query;
    if (query.coinname && query.market && query.charts) {
      this.currencyName = query.coinname;
      this.marketName = query.market;
      this.selectedCharts = query.charts.split(',');

      this.fetchAllDataAndRenderCharts();
    }
  },

  beforeDestroy() {
    Object.keys(this.timers).forEach(chart => {
      this.clearTimer(chart);
    });
  },

  methods: {

    querySearch(queryString, callback) {
      const results = queryString
          ? this.availableCurrencies.filter(currency => currency.toLowerCase().includes(queryString.toLowerCase()))
          : this.availableCurrencies;
      callback(results.map(currency => ({value: currency})));
    },

    async fetchAllDataAndRenderCharts() {
      if (this.selectedCharts.includes('Kline')) {
        await this.fetchKlineData();
      }
      if (this.selectedCharts.includes('Realtime') || this.selectedCharts.includes('Volumn')) {
        await this.fetchRealtimeAndVolumnData();
      }
      if (this.selectedCharts.includes('Marketcap')) {
        await this.fetchMarketCap();
      }
      this.renderCharts();
    },

    clearAndRerenderCharts() {

      this.selectedCharts.forEach(chart => {
        const chartDom = document.getElementById(chart);
        if (chartDom) {
          const myChart = echarts.getInstanceByDom(chartDom);
          if (myChart) {
            myChart.dispose();
          }
        }
      });

      this.$nextTick(() => {
        this.renderCharts();
      });
    },

    isChartSelected(chart) {
      return this.selectedCharts.includes(chart);
    },

    toggleChartType(chart) {
      if (chart === 'Kline') {
        this.chartTypes[chart] = this.chartTypes[chart] === 'candlestick' ? 'bar' : 'candlestick';
      } else {
        this.chartTypes[chart] = this.chartTypes[chart] === 'line' ? 'bar' : 'line';
      }
      this.renderCharts();
    },

    toggleChart(chart) {
      if (this.isChartSelected(chart)) {
        this.selectedCharts = this.selectedCharts.filter(c => c !== chart);
      } else {
        this.selectedCharts.push(chart);
        if (chart === 'Kline') {
          this.fetchKlineData().then(() => {
            this.renderCharts();
            console.log("togglechart kline");
          });
        } else if (chart === 'Realtime' || chart === 'Volumn') {
          this.fetchRealtimeAndVolumnData().then(() => {
            this.renderCharts();
            console.log(`togglechart ${chart.toLowerCase()}`);
          });
        } else if (chart === 'Marketcap') {
          this.fetchMarketCap().then(() => {
            this.renderCharts();
            console.log("togglechart marketcap");
          });
        }
      }
    },

    async handleKlineCommand(temp) {

      if (temp === '1day') {
        this.period = "30min"
      } else if (temp === '7day') {
        this.period = "4h"
      } else if (temp === '1month') {
        this.period = "4d"
      }

      await this.fetchKlineData();
      this.renderCharts();
    },

    async fetchKlineData() {

      if (!this.currencyName) {
        alert('Error: Please enter both a currency name and select a market.');
        return;
      }


      try {
        const response = await request.get('/kline', {params: {name: this.currencyName, period: this.period}});
        if (response.code === 1) {
          this.klineData = response.data;

        } else {
          console.error('Error fetching Kline data:', response.message);
        }
      } catch (error) {
        console.error('Error fetching Kline data:', error);
      }
    },

    async fetchRealtimeAndVolumnData() {

      if (!this.currencyName || !this.marketName) {
        alert('Error: Please enter both a currency name and select a market.');
        return;
      }

      try {
        const response = await request.get('/realtime/general', {
          params: {name: this.currencyName, market: this.marketName}
        });

        if (response.code === 1) {

          const filteredData = response.data.filter((item, index, array) => {
            return index === 0 || item.price !== array[index - 1].price;
          });

          // Map the filtered data to realtimeData
          this.realtimeData = filteredData.map(item => ({
            pricetime: item.pricetime,
            price: parseFloat(item.price),
            coinName: item.name,
            market: item.market
          }));

          // Map the filtered data to volumnData
          this.volumnData = filteredData.map(item => ({
            pricetime: item.pricetime,
            vol: parseFloat(item.vol),
            coinName: item.name,
            market: item.market
          }));

        } else {
          console.error('Error fetching data:', response.message);
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    },

    async fetchMarketCap() {

      if (!this.currencyName || !this.marketName) {
        alert('Error: Please enter both a currency name and select a market.');
        return;
      }

      try {
        const response = await request.get('/realtime/general', {
          params: {name: this.currencyName, market: this.marketName}
        });

        if (response.code === 1) {

          const filteredData = response.data.filter((item, index, array) => {
            return index === 0 || item.marketcap !== array[index - 1].marketcap;
          });

          // Map the filtered data to marketcapData
          this.marketcapData = filteredData.map(item => ({
            pricetime: item.pricetime,
            marketcap: parseFloat(item.marketcap),
            coinName: item.name,
            market: item.market
          }));


        } else {
          console.error('Error fetching Marketcap data:', response.message);
        }
      } catch (error) {
        console.error('Error fetching Marketcap data:', error);
      }
    },

    renderCharts() {

      const charts = this.selectedCharts.map(chart => {
        const chartDom = document.getElementById(chart);
        if (chartDom) {
          return {chart, myChart: echarts.init(chartDom)};
        }
        return null;
      }).filter(item => item !== null);

      let inAction = false;

      charts.forEach(({chart, myChart}) => {
        let option;

        if (chart === 'Kline' && this.klineData.length > 0) {
          const dates = this.klineData.map(item => item.pricetime);
          const values = this.klineData.map(item => [
            item.open, item.high, item.low, item.close
          ]);

          const primaryTimes = this.realtimeData.map(item => item.pricetime);
          const alternateTimes = this.klineData.map(item => item.pricetime);

          const times = charts.length === 1 ? alternateTimes : primaryTimes;

          const minValue = Math.min(...this.klineData.map(item => item.low));
          const maxValue = Math.max(...this.klineData.map(item => item.high));

          const resistance = Math.max(...this.klineData.map(item => item.high));
          const support = Math.min(...this.klineData.map(item => item.low));

          option = {
            title: {
              text: 'Kline Chart',
              left: '50%',
              textAlign: 'center'
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'cross',
              },
            },
            xAxis: {
              type: 'category',
              data: times,
              splitLine: {
                show: true,
              },
            },
            yAxis: {
              type: 'value',
              min: minValue,
              max: maxValue,
            },
            dataZoom: [
              {
                type: 'slider',
                show: true,
                xAxisIndex: [0],
                start: 0,
                end: 100,
              },
              {
                type: 'inside',
                xAxisIndex: [0],
                start: 0,
                end: 100,
              }
            ],
            series: [
              {
                type: 'candlestick',
                data: values,
                markLine: {
                  data: [
                    {
                      yAxis: resistance,
                      name: 'Resistance',
                      lineStyle: {
                        color: 'red',
                        type: 'dashed'
                      }
                    },
                    {
                      yAxis: support,
                      name: 'Support',
                      lineStyle: {
                        color: 'green',
                        type: 'dashed'
                      }
                    }
                  ]
                },
              },
            ],
          };

        } else if (chart === 'Realtime' && this.realtimeData.length > 0) {
          const times = this.realtimeData.map(item => item.pricetime);
          const prices = this.realtimeData.map(item => ({
            value: item.price,
            pricetime: item.pricetime,
            coinName: item.coinName,
            market: item.market
          }));

          const minValue = Math.min(...this.realtimeData.map(item => item.price));
          const maxValue = Math.max(...this.realtimeData.map(item => item.price));

          option = {
            title: {
              text: 'Realtime Chart',
              left: '50%',
              textAlign: 'center',
            },
            tooltip: {
              trigger: 'axis',
              formatter: function (params) {
                if (params.length > 0) {
                  const data = params[0].data;
                  let date = new Date(data.pricetime);
                  let hours = date.getHours().toString().padStart(2, '0');
                  let minutes = date.getMinutes().toString().padStart(2, '0');
                  let coinName = data.coinName || 'Unknown';
                  let market = data.market === '1' ? 'CoinGecko' : data.market === '3' ? 'CoinMarketCap' : 'Unknown';

                  return `Coin: ${coinName}<br/>Market: ${market}<br/>Price: ${data.value}<br/>Time: ${hours}:${minutes}`;
                }
                return '';
              },
              axisPointer: {
                animation: false,
              },
            },
            xAxis: {
              type: 'category',
              data: times,
              splitLine: {
                show: true,
              },
            },
            yAxis: {
              type: 'value',
              min: minValue,
              max: maxValue,
            },
            dataZoom: [
              {
                type: 'slider',
                show: true,
                xAxisIndex: [0],
                start: 0,
                end: 100,
              },
              {
                type: 'inside',
                xAxisIndex: [0],
                start: 0,
                end: 100,
              }
            ],
            series: [
              {
                type: this.chartTypes[chart],
                data: prices,
                large: true,
                markLine: {
                  data: [
                    {
                      yAxis: maxValue,
                      name: 'Resistance',
                      lineStyle: {
                        color: 'red',
                        type: 'dashed'
                      }
                    },
                    {
                      yAxis: minValue,
                      name: 'Support',
                      lineStyle: {
                        color: 'green',
                        type: 'dashed'
                      }
                    }
                  ]
                },
                areaStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    {offset: 0, color: 'rgba(58,77,233,0.8)'},
                    {offset: 1, color: 'rgba(58,77,233,0.3)'}
                  ])
                },
                lineStyle: {
                  color: 'rgba(58,77,233,1)',
                },
              },
            ],
          };

        } else if (chart === 'Volumn' && this.volumnData.length > 0) {
          const times = this.volumnData.map(item => item.pricetime);
          const volumn = this.volumnData.map(item => item.vol);

          const minValue = Math.min(...volumn);
          const maxValue = Math.max(...volumn);

          option = {
            title: {
              text: 'Volumn Chart',
              left: '50%',
              textAlign: 'center',
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'cross',
              },
            },
            xAxis: {
              type: 'category',
              data: times,
              splitLine: {
                show: true,
              },
            },
            yAxis: {
              type: 'value',
              min: minValue / 1e6,
              max: maxValue / 1e6,
              axisLabel: {
                formatter: function (value) {
                  return value + 'M';
                }
              }
            },
            dataZoom: [
              {
                type: 'slider',
                show: true,
                xAxisIndex: [0],
                start: 0,
                end: 100,
              },
              {
                type: 'inside',
                xAxisIndex: [0],
                start: 0,
                end: 100,
              }
            ],
            series: [
              {
                type: this.chartTypes[chart],
                large: true,
                data: volumn.map(value => value / 1e6),
                areaStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    {offset: 0, color: 'rgba(58,77,233,0.8)'},
                    {offset: 1, color: 'rgba(58,77,233,0.3)'}
                  ])
                },
                lineStyle: {
                  color: 'rgba(58,77,233,1)',
                },
              },
            ],
          };

        } else if (chart === 'Marketcap' && this.marketcapData.length > 0) {
          const time = this.marketcapData.map(item => item.pricetime);
          const marketcap = this.marketcapData.map(item => item.marketcap);
          const primaryTimes = this.realtimeData.map(item => item.pricetime);

          const times = charts.length === 1 ? time : primaryTimes;

          const validMarketcap = this.marketcapData
              .map(item => parseFloat(item.marketcap))
              .filter(value => !isNaN(value) && value !== null && value !== undefined);

          const minValue = Math.min(...validMarketcap);
          const maxValue = Math.max(...validMarketcap);


          option = {
            title: {
              text: 'MarketCap Chart',
              left: '50%',
              textAlign: 'center',
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'cross',
              },
            },
            xAxis: {
              type: 'category',
              data: times,
              splitLine: {
                show: true,
              },
            },
            yAxis: {
              type: 'value',
              splitLine: {
                show: true,
              },
              min: minValue / 1e6,
              max: maxValue / 1e6,
              axisLabel: {
                formatter: function (value) {
                  return value + 'M';
                }
              }
            },
            dataZoom: [
              {
                type: 'slider',
                show: true,
                xAxisIndex: [0],
                start: 0,
                end: 100,
              },
              {
                type: 'inside',
                xAxisIndex: [0],
                start: 0,
                end: 100,
              }
            ],
            series: [
              {
                type: this.chartTypes[chart],
                large: true,
                data: validMarketcap.map(value => value / 1e6),
                areaStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    {offset: 0, color: 'rgba(58,77,233,0.8)'},
                    {offset: 1, color: 'rgba(58,77,233,0.3)'}
                  ])
                },
                lineStyle: {
                  color: 'rgba(58,77,233,1)',
                },
              },
            ],
          };
        }

        option.graphic = {
          type: 'group',
          left: '90%',
          top: '5%',
          children: [
            {
              type: 'rect',
              left: 'center',
              top: 'center',
              z: 100,
              shape: {
                width: 20,
                height: 20,
              },
              style: {
                fill: '#fff',
                stroke: '#888',
                lineWidth: 1,
                shadowBlur: 5,
                shadowColor: 'rgba(0, 0, 0, 0.3)',
                shadowOffsetX: 3,
                shadowOffsetY: 3,
                cursor: 'pointer'
              },
              onclick: () => {
                this.handleChartClick(chart, option);
              }
            },
            {
              type: 'text',
              left: 'center',
              top: 'center',
              z: 100,
              style: {
                text: '+',
                fill: '#000',
                font: '14px sans-serif',
                textAlign: 'center',
                textVerticalAlign: 'middle',
              },
              onclick: () => {
                this.handleChartClick(chart, option);
              }
            }
          ]
        };

        // Apply the option to the chart
        option && myChart.setOption(option, {notMerge: true});

        // Set up event listener for dataZoom
        myChart.on('dataZoom', params => {
          if (!inAction && params.batch && params.batch[0]) {
            inAction = true;
            charts.forEach(({chart: otherChart, myChart: otherMyChart}) => {
              if (otherChart !== chart) {
                otherMyChart.dispatchAction({
                  type: 'dataZoom',
                  start: params.batch[0].start,
                  end: params.batch[0].end,
                });
              }
            });
            inAction = false;
          }
        });

        // Set up event listener for axisPointer (showTip event)
        myChart.on('showTip', (params) => {
          if (!inAction) {
            inAction = true;
            charts.forEach(({myChart}) => {
              if (myChart !== params.chart) {
                myChart.dispatchAction({
                  type: 'showTip',
                  seriesIndex: 0,
                  dataIndex: params.dataIndex
                });
              }
            });
            inAction = false;
          }
        });

        myChart.on('hideTip', () => {
          if (!inAction) {
            inAction = true;
            charts.forEach(({myChart}) => {
              myChart.dispatchAction({
                type: 'hideTip'
              });
            });
            inAction = false;
          }
        });

      });
    },

    handleChartClick(chart, option) {

      this.selectedChartData = {chart, option};
      this.dialogVisible = true;

      this.$nextTick(() => {
        const dialogChartDom = document.getElementById('dialog-chart');
        if (dialogChartDom) {
          const dialogChart = echarts.init(dialogChartDom);


          option.toolbox = {
            feature: {
              saveAsImage: {show: true},
              dataZoom: {show: true},
              restore: {show: true},
              dataView: {show: true, readOnly: false},
              magicType: {
                type: ['line', 'bar']
              },
            },
          };

          dialogChart.setOption(this.selectedChartData.option);
        }
      });
    },

    handleClose() {

      const dialogChartDom = document.getElementById('dialog-chart');
      if (dialogChartDom) {
        const dialogChart = echarts.getInstanceByDom(dialogChartDom);
        if (dialogChart) {
          dialogChart.dispose();
        }
      }
      this.dialogVisible = false;
    },

    navigateTo(route) {
      if (this.$route.path !== route) {
        this.$router.push(route).then(() => {
          window.location.reload();
        }).catch(err => {
          if (err.name !== 'NavigationDuplicated') {
            console.error(err);
          }
        });
      }
    },

    showHowToUse() {
      this.howToUseVisible = true;
    },

    async saveData() {
      // Give warning if currencyName, marketName, and selectedCharts are not empty
      if (!this.currencyName || !this.marketName || this.selectedCharts.length === 0) {
        alert('Error: Currency name, market, and charts must be selected.');
        this.$message.error('Please make sure all fields are filled out.');
        return;
      }

      try {
        // Retrieve the JWT token from local storage
        const token = localStorage.getItem('token');
        let username = '';
        let userid = 0;

        if (token) {
          // Decode the JWT token to extract the payload
          const decodedToken = jwtDecode(token);
          username = decodedToken.name;
          userid = decodedToken.id;

        }

        // Convert selectedCharts array to a comma-separated string and prepare the data to be sent
        const data = {
          username: username,
          userid: userid,
          coinname: this.currencyName,
          market: this.marketName,
          charts: this.selectedCharts.join(',')  // Convert array to comma-separated string
        };


        // Send request to API
        const response = await request.post('/savecharts', null, {params: data});

        // Handle response
        if (response.code === 1) {
          this.$message.success('Data has been successfully saved.');
        } else {
          this.$message.error('There was an issue saving the data.');
        }
      } catch (error) {
        console.error('Error saving data:', error);
        this.$message.error('An error occurred while saving the data.');
      }
    },

    // This is a timer for updating. The functionality has been done.
    // However, because the scale of data is larger when more charts are selected， The page will be not responding. Thus, I close this functionality.
    // (Severe lag won't occur on the webpage as long as at most two charts are selected)
    setupTimer() {
      if (this.timers['update']) {
        return; // Timer already running
      }

      console.log('Timer is open');

      this.timers['update'] = setInterval(async () => {
        console.log('Updating');

        // Fetch data and re-render charts
        await this.updateAndRenderCharts();
      }, 60000); // 1 minute interval
    },

    clearTimer() {
      if (this.timers['update']) {
        clearInterval(this.timers['update']);
        this.timers['update'] = null;
      }
    },

    async updateAndRenderCharts() {
      if (this.selectedCharts.includes('Kline')) {
        await this.fetchKlineData();
      }

      if (this.selectedCharts.includes('Realtime') || this.selectedCharts.includes('Volumn')) {
        await this.fetchRealtimeAndVolumnData();
      }

      if (this.selectedCharts.includes('Marketcap')) {
        await this.fetchMarketCap();
      }

      this.renderCharts(); // Fetch all data and render the charts
    },


  },
};
</script>



