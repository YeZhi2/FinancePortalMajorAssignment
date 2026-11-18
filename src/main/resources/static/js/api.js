/* =========================================================
   统一请求封装与通用 UI 工具
   ========================================================= */

const API = {
    async request(path, options = {}) {
        const config = {
            credentials: 'same-origin',
            headers: { 'Content-Type': 'application/json' },
            ...options
        };
        let response;
        try {
            response = await fetch(path, config);
        } catch (e) {
            throw new Error('网络异常，请确认服务已启动');
        }
        let body = null;
        try {
            body = await response.json();
        } catch (e) {
            throw new Error('服务响应格式异常（HTTP ' + response.status + '）');
        }
        if (!response.ok || (body && body.code !== 0)) {
            const error = new Error((body && body.message) || ('请求失败（HTTP ' + response.status + '）'));
            error.status = response.status;
            error.code = body && body.code;
            throw error;
        }
        return body ? body.data : null;
    },

    get(path) {
        return this.request(path);
    },

    post(path, data) {
        return this.request(path, { method: 'POST', body: data ? JSON.stringify(data) : undefined });
    },

    put(path, data) {
        return this.request(path, { method: 'PUT', body: data ? JSON.stringify(data) : undefined });
    },

    patch(path, data) {
        return this.request(path, { method: 'PATCH', body: data ? JSON.stringify(data) : undefined });
    },

    del(path) {
        return this.request(path, { method: 'DELETE' });
    }
};

const RISK_LABELS = { R1: '低风险', R2: '中低风险', R3: '中风险', R4: '中高风险', R5: '高风险' };

const UI = {
    escape(value) {
        if (value === null || value === undefined) {
            return '';
        }
        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    },

    money(value) {
        const number = Number(value || 0);
        return number.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    },

    int(value) {
        return Number(value || 0).toLocaleString('zh-CN');
    },

    date(value) {
        if (!value) {
            return '—';
        }
        return String(value).slice(0, 10);
    },

    datetime(value) {
        if (!value) {
            return '—';
        }
        return String(value).replace('T', ' ').slice(0, 16);
    },

    riskBadge(level) {
        if (!level) {
            return '<span class="badge badge-gray">未评级</span>';
        }
        const key = level.toUpperCase();
        return '<span class="badge badge-' + key.toLowerCase() + '">' + key + ' ' + (RISK_LABELS[key] || '') + '</span>';
    },

    termText(termDays) {
        return termDays && termDays > 0 ? termDays + ' 天' : '活期';
    },

    progress(product) {
        const total = Number(product.totalAmount || 0);
        if (total <= 0) {
            return 0;
        }
        return Math.min(100, (Number(product.soldAmount || 0) / total) * 100);
    },

    productCard(product) {
        const progress = this.progress(product).toFixed(1);
        return `
        <article class="product-card">
            <div class="product-head">
                <div>
                    <div class="product-name">${this.escape(product.name)}</div>
                    <div class="product-code">${this.escape(product.code)} · ${this.escape(product.category)}</div>
                </div>
                ${this.riskBadge(product.riskLevel)}
            </div>
            <div class="product-rate">${product.expectedReturn}<small>% 预期年化</small></div>
            <div class="product-meta">
                <div><b>${this.termText(product.termDays)}</b>投资期限</div>
                <div><b>${this.money(product.minAmount)}</b>起投金额</div>
                <div><b>${progress}%</b>募集进度</div>
            </div>
            <div class="product-foot">
                <span class="hint">${this.escape(product.manager || '—')}</span>
                <a class="btn btn-sm btn-outline" href="detail.html?id=${product.id}">查看详情</a>
            </div>
        </article>`;
    },

    orderStatusBadge(status) {
        return status === 'PAID'
            ? '<span class="badge badge-green">持有中</span>'
            : '<span class="badge badge-gray">已赎回</span>';
    },

    empty(text) {
        return '<div class="empty">' + this.escape(text || '暂无数据') + '</div>';
    },

    toast(message, type) {
        let box = document.querySelector('.toast-box');
        if (!box) {
            box = document.createElement('div');
            box.className = 'toast-box';
            document.body.appendChild(box);
        }
        const item = document.createElement('div');
        item.className = 'toast' + (type ? ' ' + type : '');
        item.textContent = message;
        box.appendChild(item);
        setTimeout(() => item.remove(), 2800);
    },

    loginUrl() {
        return 'login.html?redirect=' + encodeURIComponent(location.pathname.split('/').pop() + location.search);
    },

    /**
     * 统一处理登录失效：跳转登录页。
     */
    handleError(error) {
        if (error && (error.status === 401 || error.code === 401)) {
            location.href = UI.loginUrl();
            return;
        }
        UI.toast(error.message || '操作失败', 'error');
    }
};
