function updateMultiplication() {
  $.ajax({
    url: "http://localhost:8080/multiplications/random",
  }).then(data => {
    //폼 비우기
    $("#attempt-form")
      .find("input[name='result-attempt'], input[name='user-alias']")
      .val("");
    //무작위 문제를 api 로 가져와서 추가하기
    $(".multiplication-a").empty().append(data.factorA);
    $(".multiplication-b").empty().append(data.factorB);
  });
}

function updateResults(alias) {
  let userId = -1;
  $.ajax({
    url: "http://localhost:8080/results?alias="+alias,
    async: false,
    success: data => {
      $('#results-div').show();
      $('#results-body').empty();
      data.forEach(row => {
        $('#results-body').append(
            '<tr>'+
            '<td>'+ row.id +'</td>' +
            '<td>'+ row.multiplication.factorA + ' x ' + row.multiplication.factorB + '</td>' +
            '<td>'+ row.resultAttempt +'</td>' +
            '<td>'+ (row.correct === true ? 'YES' : 'NO') +'</td>' +
            '</tr>'
        )
      })
      userId = data[0].user.id;
    }
  })
  return userId;
}

$(document).ready(() => {
  updateMultiplication();

  //폼 기본 제출 막기
  $("#attempt-form").submit(event => {
    event.preventDefault();
    let a = $(".multiplication-a").text();
    let b = $(".multiplication-b").text();
    // let $form = $(this),
    //   attempt = $form.find("input[name='result-attempt']").val(),
    //   userAlias = $form.find("input[name='user-alias']").val();
    let attempt = $("input[name='result-attempt']").val();
    let userAlias = $("input[name='user-alias']").val();
    //json
    let data = {
      id: null,
      user: {
        id: null,
        alias: userAlias,
      },
      multiplication: {
        id: null,
        factorA: a,
        factorB: b,
      },
      resultAttempt: attempt,
      correct: false,
    };

    $.ajax({
      url: "http://localhost:8080/results",
      type: "POST",
      data: JSON.stringify(data),
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function (result) {
        if (result.correct) {
          $(".result-message").empty().append(
              "<p class='bg-success text-center'>정답입니다! 축하드려요!</p>"
          );
        } else {
          $(".result-message").empty().append(
              "<p class='bg-danger text-center'>오답입니다! 그래도 포기하지마세요! </p>"
          );
        }
      },
    }).then(() => {
      updateMultiplication();
      setTimeout(() => {
        let userId = updateResults(userAlias)
        updateStats(userId);
        updateLeaderBoard();
      })
    })
  })
})