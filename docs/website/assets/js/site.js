/* Config Tuner — site behaviour. No dependencies, no network, no cookies. */
(function () {
  "use strict";

  /* ---------- 1. supported-titles filter ---------- */
  function initTitleFilter() {
    var input = document.getElementById("title-filter");
    var grid = document.getElementById("title-list");
    var count = document.getElementById("title-count");
    var reset = document.getElementById("title-reset");
    var empty = document.getElementById("title-empty");
    if (!input || !grid) return;

    var cards = Array.prototype.slice.call(grid.querySelectorAll(".title-card"));
    var total = cards.length;

    // index once: haystack = visible name + package id + control count
    var data = cards.map(function (card) {
      var name = (card.querySelector(".tc-name") || {}).textContent || "";
      var pkg = (card.querySelector(".tc-pkg") || {}).textContent || "";
      return { el: card, hay: (name + " " + pkg).toLowerCase() };
    });

    function apply() {
      var q = input.value.trim().toLowerCase();
      var shown = 0;
      data.forEach(function (row) {
        var hit = !q || row.hay.indexOf(q) !== -1;
        row.el.hidden = !hit;
        if (hit) shown++;
      });
      if (count) {
        count.textContent = shown + " of " + total + " shown";
      }
      if (empty) empty.hidden = shown !== 0;
      grid.dataset.filtered = q ? "true" : "false";
    }

    input.addEventListener("input", apply);
    input.addEventListener("search", apply);
    input.addEventListener("keydown", function (e) {
      if (e.key === "Escape" && input.value) {
        input.value = "";
        apply();
      }
    });
    if (reset) {
      reset.addEventListener("click", function () {
        input.value = "";
        apply();
        input.focus();
      });
    }
    apply();
  }

  /* ---------- 2. screenshot tabs (WAI-ARIA tabs, roving tabindex) ---------- */
  function initShotTabs() {
    var tablist = document.querySelector(".shot-tabs");
    if (!tablist) return;
    var tabs = Array.prototype.slice.call(tablist.querySelectorAll('[role="tab"]'));
    if (!tabs.length) return;

    function panelFor(tab) {
      return document.getElementById(tab.getAttribute("aria-controls"));
    }

    function select(tab, focus) {
      tabs.forEach(function (t) {
        var on = t === tab;
        t.setAttribute("aria-selected", on ? "true" : "false");
        t.tabIndex = on ? 0 : -1;
        var panel = panelFor(t);
        if (panel) panel.hidden = !on;
      });
      if (focus) tab.focus();
    }

    tabs.forEach(function (tab) {
      tab.addEventListener("click", function () { select(tab, false); });
      tab.addEventListener("keydown", function (e) {
        var i = tabs.indexOf(tab);
        var next = null;
        if (e.key === "ArrowRight") next = tabs[(i + 1) % tabs.length];
        else if (e.key === "ArrowLeft") next = tabs[(i - 1 + tabs.length) % tabs.length];
        else if (e.key === "Home") next = tabs[0];
        else if (e.key === "End") next = tabs[tabs.length - 1];
        if (next) { e.preventDefault(); select(next, true); }
      });
    });

    // initial state: first tab unless the markup already selects one
    var initial = tabs.filter(function (t) {
      return t.getAttribute("aria-selected") === "true";
    })[0] || tabs[0];
    select(initial, false);
  }

  /* ---------- 3. restrained reveal ---------- */
  function initReveal() {
    var targets = document.querySelectorAll(
      ".steps li, .cards-3 .card, .title-card, .caveats, .pipeline, .sec-head, .req-panel"
    );
    if (!targets.length) return;
    var reduce = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    if (reduce || !("IntersectionObserver" in window)) return;

    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-in");
          io.unobserve(entry.target);
        }
      });
    }, { rootMargin: "0px 0px -8% 0px", threshold: 0.08 });

    Array.prototype.forEach.call(targets, function (el) {
      el.classList.add("reveal");
      io.observe(el);
    });
  }

  /* ---------- 4. nav: mark the section being read ---------- */
  function initNavSpy() {
    var links = Array.prototype.slice.call(document.querySelectorAll(".site-nav a[href^='#']"));
    if (!links.length || !("IntersectionObserver" in window)) return;
    var map = {};
    links.forEach(function (a) {
      var id = a.getAttribute("href").slice(1);
      var sec = document.getElementById(id);
      if (sec) map[id] = a;
    });
    var ids = Object.keys(map);
    if (!ids.length) return;
    var seen = {};
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        seen[entry.target.id] = entry.isIntersecting;
      });
      var active = ids.filter(function (id) { return seen[id]; })[0];
      links.forEach(function (a) { a.removeAttribute("aria-current"); });
      if (active) map[active].setAttribute("aria-current", "true");
    }, { rootMargin: "-20% 0px -70% 0px" });
    ids.forEach(function (id) {
      var sec = document.getElementById(id);
      if (sec) io.observe(sec);
    });
  }

  /* ---------- boot ---------- */
  function boot() {
    initTitleFilter();
    initShotTabs();
    initReveal();
    initNavSpy();
    document.documentElement.classList.add("js-ready");
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", boot);
  } else {
    boot();
  }
})();
