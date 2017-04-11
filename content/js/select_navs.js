(function() {
  'use strict'

  var removeVisible = function(target) {
    var visibles = target.querySelectorAll('.visible')
    for (var i = 0; i < visibles.length; i++) {
      var visible = visibles[i]
      visible.classList.remove('visible')
    }
  }

  var randomFromArray = function (array) {
    return array[Math.floor(Math.random() * array.length)]
  }

  document.addEventListener('DOMContentLoaded', function () {
    var target = document.getElementById('nav-right')
    var selectedName = target.getAttribute('data-selected')
    removeVisible(target);
    var selected
    if (selectedName == '') {
      selected = randomFromArray(target.children)
    } else {
      selected = target.querySelector('[data-name=' + selectedName + ']')
    }
    selected.classList.add('visible')
  })
})()
