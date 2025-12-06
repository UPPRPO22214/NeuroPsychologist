import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import { AnalyticsService } from '../services/analytics.service';
import type { MetricsData } from '../services/analytics.service';
import '../styles/Analytics.css';

const AnalyticsPage = () => {
  const [metrics, setMetrics] = useState<MetricsData[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  // Calculate default dates (last 7 days)
  useEffect(() => {
    const end = new Date();
    const start = new Date();
    start.setDate(start.getDate() - 7);
    
    setEndDate(end.toISOString().split('T')[0]);
    setStartDate(start.toISOString().split('T')[0]);
  }, []);

  // Load metrics when dates change
  useEffect(() => {
    if (startDate && endDate) {
      loadMetrics();
    }
  }, [startDate, endDate]);

  const loadMetrics = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await AnalyticsService.getMetrics(startDate, endDate);
      setMetrics(data);
    } catch (err) {
      setError('Не удалось загрузить данные. Попробуйте снова.');
      console.error('Error loading metrics:', err);
    } finally {
      setLoading(false);
    }
  };

  // Calculate averages for check-in metrics
  const calculateAverages = () => {
    const checkIns = metrics.filter(m => m.isCheckin);
    if (checkIns.length === 0) return null;

    const sum = {
      calmness: 0,
      energy: 0,
      satisfaction: 0,
      connection: 0,
      engagement: 0,
    };

    checkIns.forEach(m => {
      sum.calmness += m.calmnessRating || 0;
      sum.energy += m.energyRating || 0;
      sum.satisfaction += m.satisfactionRating || 0;
      sum.connection += m.connectionRating || 0;
      sum.engagement += m.engagementRating || 0;
    });

    return {
      calmness: (sum.calmness / checkIns.length).toFixed(1),
      energy: (sum.energy / checkIns.length).toFixed(1),
      satisfaction: (sum.satisfaction / checkIns.length).toFixed(1),
      connection: (sum.connection / checkIns.length).toFixed(1),
      engagement: (sum.engagement / checkIns.length).toFixed(1),
    };
  };

  const averages = calculateAverages();

  // Prepare data for charts
  const prepareChartData = () => {
    const checkIns = metrics.filter(m => m.isCheckin).reverse(); // Reverse to show oldest first
    
    return {
      dates: checkIns.map(m => new Date(m.analyzedAt).toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' })),
      calmness: checkIns.map(m => m.calmnessRating || 0),
      energy: checkIns.map(m => m.energyRating || 0),
      satisfaction: checkIns.map(m => m.satisfactionRating || 0),
      connection: checkIns.map(m => m.connectionRating || 0),
      engagement: checkIns.map(m => m.engagementRating || 0),
    };
  };

  const chartData = prepareChartData();

  // Render line chart
  const renderLineChart = (data: number[], label: string, color: string) => {
    if (data.length === 0) return null;

    const maxValue = 5;
    const chartHeight = 200;
    const chartWidth = 600;
    const padding = { top: 20, right: 20, bottom: 40, left: 40 };
    const innerWidth = chartWidth - padding.left - padding.right;
    const innerHeight = chartHeight - padding.top - padding.bottom;

    // Calculate points
    const points = data.map((value, index) => {
      const x = padding.left + (index / (data.length - 1 || 1)) * innerWidth;
      const y = padding.top + innerHeight - (value / maxValue) * innerHeight;
      return { x, y, value };
    });

    // Create path
    const pathData = points.map((point, index) => 
      `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`
    ).join(' ');

    return (
      <div className="chart-container" style={{ marginBottom: '2rem' }}>
        <h3 className="text-text-primary text-lg font-semibold mb-4">{label}</h3>
        <svg width={chartWidth} height={chartHeight} style={{ overflow: 'visible' }}>
          {/* Grid lines */}
          {[1, 2, 3, 4, 5].map(value => {
            const y = padding.top + innerHeight - (value / maxValue) * innerHeight;
            return (
              <g key={value}>
                <line
                  x1={padding.left}
                  y1={y}
                  x2={chartWidth - padding.right}
                  y2={y}
                  stroke="#e5e7eb"
                  strokeWidth="1"
                />
                <text
                  x={padding.left - 10}
                  y={y + 4}
                  textAnchor="end"
                  fontSize="12"
                  fill="#6b7280"
                >
                  {value}
                </text>
              </g>
            );
          })}

          {/* X-axis labels */}
          {chartData.dates.map((date, index) => {
            const x = padding.left + (index / (chartData.dates.length - 1 || 1)) * innerWidth;
            return (
              <text
                key={index}
                x={x}
                y={chartHeight - padding.bottom + 20}
                textAnchor="middle"
                fontSize="12"
                fill="#6b7280"
              >
                {date}
              </text>
            );
          })}

          {/* Line */}
          <path
            d={pathData}
            fill="none"
            stroke={color}
            strokeWidth="3"
            strokeLinecap="round"
            strokeLinejoin="round"
          />

          {/* Points */}
          {points.map((point, index) => (
            <g key={index}>
              <circle
                cx={point.x}
                cy={point.y}
                r="5"
                fill={color}
                stroke="white"
                strokeWidth="2"
              />
              <title>{`${chartData.dates[index]}: ${point.value}`}</title>
            </g>
          ))}

          {/* Axes */}
          <line
            x1={padding.left}
            y1={padding.top}
            x2={padding.left}
            y2={chartHeight - padding.bottom}
            stroke="#374151"
            strokeWidth="2"
          />
          <line
            x1={padding.left}
            y1={chartHeight - padding.bottom}
            x2={chartWidth - padding.right}
            y2={chartHeight - padding.bottom}
            stroke="#374151"
            strokeWidth="2"
          />
        </svg>
      </div>
    );
  };

  return (
    <div className="relative flex h-auto min-h-screen w-full flex-col bg-background-primary" style={{ fontFamily: 'Inter, "Noto Sans", sans-serif' }}>
      <div className="flex h-full grow flex-col">
        <Header />
        
        <main className="flex-1 p-4 sm:p-6 lg:p-8" style={{ paddingTop: 'calc(1rem + 60px)' }}>
          <div className="w-full max-w-7xl mx-auto">
            {/* Header */}
            <div className="mb-6">
              <h1 className="text-text-primary text-3xl font-bold leading-tight mb-2">Аналитика</h1>
              <p className="text-text-secondary text-base">Отслеживайте свои метрики и прогресс</p>
            </div>

            {/* Date Range Selector */}
            <div className="bg-background-secondary rounded-xl shadow-sm p-6 mb-6">
              <h2 className="text-text-primary text-lg font-semibold mb-4">Период</h2>
              <div className="flex flex-wrap gap-4 items-end">
                <div className="flex-1 min-w-[200px]">
                  <label className="block text-text-secondary text-sm mb-2">Начало</label>
                  <input
                    type="date"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                    className="w-full px-4 py-2 rounded-lg border border-surface-primary bg-surface-secondary text-text-primary focus:outline-none focus:ring-2 focus:ring-brand-primary"
                  />
                </div>
                <div className="flex-1 min-w-[200px]">
                  <label className="block text-text-secondary text-sm mb-2">Конец</label>
                  <input
                    type="date"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                    className="w-full px-4 py-2 rounded-lg border border-surface-primary bg-surface-secondary text-text-primary focus:outline-none focus:ring-2 focus:ring-brand-primary"
                  />
                </div>
                <button
                  onClick={loadMetrics}
                  className="px-6 py-2 bg-brand-primary text-white rounded-lg hover:bg-brand-primary-darker transition-colors"
                >
                  Применить
                </button>
              </div>
            </div>

            {/* Loading State */}
            {loading && (
              <div className="flex justify-center items-center py-12">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-brand-primary"></div>
              </div>
            )}

            {/* Error State */}
            {error && (
              <div className="bg-red-50 border border-red-200 rounded-xl p-4 mb-6">
                <p className="text-red-600">{error}</p>
              </div>
            )}

            {/* No Data State */}
            {!loading && !error && metrics.length === 0 && (
              <div className="bg-background-secondary rounded-xl shadow-sm p-12 text-center">
                <p className="text-text-secondary text-lg">Нет данных за выбранный период</p>
              </div>
            )}

            {/* Metrics Display */}
            {!loading && !error && metrics.length > 0 && (
              <>

                {/* Charts */}
                {chartData.dates.length > 0 && (
                  <div className="bg-background-secondary rounded-xl shadow-sm p-6 mb-6">
                    <h2 className="text-text-primary text-xl font-semibold mb-6">Динамика показателей</h2>
                    <div className="space-y-8">
                      {renderLineChart(chartData.calmness, 'Спокойствие', '#8B5CF6')}
                      {renderLineChart(chartData.energy, 'Энергия', '#F59E0B')}
                      {renderLineChart(chartData.satisfaction, 'Удовлетворённость', '#10B981')}
                      {renderLineChart(chartData.connection, 'Связь с людьми', '#3B82F6')}
                      {renderLineChart(chartData.engagement, 'Вовлечённость', '#EC4899')}
                    </div>
                  </div>
                )}

                {/* Average Ratings */}
                {averages && (
                  <div className="bg-background-secondary rounded-xl shadow-sm p-6">
                    <h2 className="text-text-primary text-xl font-semibold mb-6">Средние показатели</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
                      <div className="text-center p-4 bg-surface-secondary rounded-lg">
                        <div className="text-3xl font-bold text-purple-600 mb-1">{averages.calmness}</div>
                        <div className="text-sm text-text-secondary">Спокойствие</div>
                      </div>
                      <div className="text-center p-4 bg-surface-secondary rounded-lg">
                        <div className="text-3xl font-bold text-amber-600 mb-1">{averages.energy}</div>
                        <div className="text-sm text-text-secondary">Энергия</div>
                      </div>
                      <div className="text-center p-4 bg-surface-secondary rounded-lg">
                        <div className="text-3xl font-bold text-green-600 mb-1">{averages.satisfaction}</div>
                        <div className="text-sm text-text-secondary">Удовлетворённость</div>
                      </div>
                      <div className="text-center p-4 bg-surface-secondary rounded-lg">
                        <div className="text-3xl font-bold text-blue-600 mb-1">{averages.connection}</div>
                        <div className="text-sm text-text-secondary">Связь</div>
                      </div>
                      <div className="text-center p-4 bg-surface-secondary rounded-lg">
                        <div className="text-3xl font-bold text-pink-600 mb-1">{averages.engagement}</div>
                        <div className="text-sm text-text-secondary">Вовлечённость</div>
                      </div>
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
        </main>
      </div>
    </div>
  );
};

export default AnalyticsPage;