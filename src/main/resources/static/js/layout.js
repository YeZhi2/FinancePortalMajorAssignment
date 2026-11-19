/* =========================================================
   公共布局：顶部导航（含登录态）与页脚
   ========================================================= */

const NAV_ITEMS = [
    { href: 'index.html', label: '首页', key: 'index' },
    { href: 'products.html', label: '产品中心', key: 'products' },
    { href: 'risk.html', label: '风险测评', key: 'risk' },
    { href: 'portfolio.html', label: '我的持仓', key: 'portfolio' }
];

/**
 * 渲染公共头部与页脚，并返回当前登录用户（未登录返回 null）。
 */
async function initLayout(activeKey) {
    let me = null;
    try {
        me = await API.get('/api/auth/me');
    } catch (e) {
        me = null;
    }
    window.__ME__ = me;

    const links = NAV_ITEMS.map(item => {
        const active = item.key === activeKey ? ' class="active"' : '';
        return `<a href="${item.href}"${active}>${item.label}</a>`;
    });
    if (me && me.role === 'ADMIN') {
        links.push(`<a href="admin.html"${activeKey === 'admin' ? ' class="active"' : ''}>管理后台</a>`);
    }

    const userArea = me
        ? `<span class="who">你好，<strong>${UI.escape(me.realName || me.username)}</strong></span>
           <button class="btn btn-sm btn-ghost" id="logout-btn" type="button">退出</button>`
        : `<a class="btn btn-sm btn-ghost" href="login.html">登录</a>
           <a class="btn btn-sm btn-accent" href="register.html">注册</a>`;

    const header = document.getElementById('site-header');
    header.className = 'site-header';
    header.innerHTML = `
        <div class="nav-inner">
            <a class="brand" href="index.html">
                <span class="brand-mark">金</span>汇金财富
            </a>
            <nav class="nav-links">${links.join('')}</nav>
            <div class="nav-user">${userArea}</div>
        </div>`;

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            try {
                await API.post('/api/auth/logout');
            } catch (e) {
                // 忽略登出异常，直接回首页
            }
            location.href = 'index.html';
        });
    }

    if (!document.querySelector('.site-footer')) {
        const footer = document.createElement('footer');
        footer.className = 'site-footer';
        footer.innerHTML = `
            <div><strong>汇金财富</strong> · 金融理财产品门户（课程作业演示项目）</div>
            <div style="margin-top:6px">本平台为教学演示，页面数据与收益均为模拟，不构成任何投资建议。</div>`;
        document.body.appendChild(footer);
    }

    return me;
}
