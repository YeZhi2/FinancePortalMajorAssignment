function initProductComparison() {
    const minimum = 2;
    const maximum = 3;
    const selected = new Map();
    let visibleProducts = new Map();
    const list = document.getElementById('product-list');
    const count = document.getElementById('comparison-count');
    const picks = document.getElementById('comparison-picks');
    const clearButton = document.getElementById('comparison-clear');
    const openButton = document.getElementById('comparison-open');
    const dialog = document.getElementById('comparison-dialog');
    const table = document.getElementById('comparison-table');

    function renderSelection() {
        count.textContent = `已选 ${selected.size} / ${maximum} 款；请选择 ${minimum}–${maximum} 款`;
        clearButton.disabled = selected.size === 0;
        openButton.disabled = selected.size < minimum;
        picks.innerHTML = Array.from(selected, ([id, product]) => `
            <button class="btn btn-sm btn-outline comparison-pick" type="button" data-remove-id="${UI.escape(id)}"
                aria-label="移除 ${UI.escape(product.name)}">${UI.escape(product.name)} <span aria-hidden="true">×</span></button>
        `).join('');
        list.querySelectorAll('[data-compare-id]').forEach(button => {
            button.setAttribute('aria-pressed', String(selected.has(button.dataset.compareId)));
        });
    }

    list.addEventListener('click', event => {
        const button = event.target.closest('[data-compare-id]');
        if (!button) {
            return;
        }
        const id = button.dataset.compareId;
        if (selected.has(id)) {
            selected.delete(id);
        } else if (selected.size >= maximum) {
            UI.toast(`最多对比 ${maximum} 款产品，请先移除一款`, 'error');
            return;
        } else if (visibleProducts.has(id)) {
            selected.set(id, visibleProducts.get(id));
        }
        renderSelection();
    });

    picks.addEventListener('click', event => {
        const button = event.target.closest('[data-remove-id]');
        if (button) {
            selected.delete(button.dataset.removeId);
            renderSelection();
            (picks.querySelector('button') || count).focus();
        }
    });

    clearButton.addEventListener('click', () => {
        selected.clear();
        renderSelection();
        count.focus();
    });

    openButton.addEventListener('click', () => {
        if (selected.size < minimum) {
            return;
        }
        const products = Array.from(selected.values());
        const rows = [
            ['产品类型', product => UI.escape(product.category || '—')],
            ['风险等级', product => UI.riskBadge(/^R[1-5]$/.test(product.riskLevel) ? product.riskLevel : null)],
            ['预期年化收益率', product => product.expectedReturn == null ? '—' : `${UI.escape(product.expectedReturn)}%`],
            ['投资期限', product => UI.escape(UI.termText(product.termDays))],
            ['起投金额', product => `${UI.money(product.minAmount)} 元`],
            ['产品管理人', product => UI.escape(product.manager || '—')],
            ['产品详情', product => `<a class="btn btn-sm btn-outline" href="detail.html?id=${encodeURIComponent(product.id)}">查看详情</a>`]
        ];
        table.innerHTML = `<table>
            <thead><tr><th scope="col">对比项目</th>${products.map(product => `
                <th scope="col"><strong>${UI.escape(product.name)}</strong><br>${UI.escape(product.code)}</th>`).join('')}</tr></thead>
            <tbody>${rows.map(([label, format]) => `<tr><th scope="row">${label}</th>${products.map(product =>
                `<td>${format(product)}</td>`).join('')}</tr>`).join('')}</tbody>
        </table>`;
        dialog.showModal();
    });

    return {
        setVisibleProducts(products) {
            visibleProducts = new Map(products.map(product => [String(product.id), product]));
            visibleProducts.forEach((product, id) => {
                if (selected.has(id)) {
                    selected.set(id, product);
                }
            });
            renderSelection();
        },
        isSelected(id) {
            return selected.has(String(id));
        }
    };
}
