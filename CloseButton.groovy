//Автор: aseleznev
//Дата создания: 20.07.2021
//Код:
//Назначение:
/**
 * Скрипт для закрытия заявки по вызову пользовательского ДПС
 */
//Версия: 4.11.0.16
//Категория: Скрипт действия по событию

//ПАРАМЕТРЫ ---------------------------------
SCFQN = 'serviceCall$request'     //Метакласс создаваемого запроса
SHORTDESCR = 'ПЕРЕСМЕНКА/ПЕРЕРЫВ'
//Тема создаваемого запроса
BAD_STATES = ['resolved','closed']   //Статусы, которые должны игнорироваться при поиске заявок

//ОСНОВНОЙ БЛОК ------------------------------

if (user)
{
    def foundedSC = utils.find(SCFQN,['client': user, 'state' : op.not(BAD_STATES), 'shortDescr' : SHORTDESCR])
    if(foundedSC)
    {
        if(foundedSC.size()>1)
        {
            return result.showMessage('Ошибка', "Найдено более одной заявки. Найденные заявки ${foundedSC.UUID.join(',')}")
        }
        else
        {
            utils.create('comment', ['author': user,  'text' : "Простой завершён ${utils.formatters.formatDateTime(new Date())}", 'source' : foundedSC[0]])
            utils.edit(foundedSC.first(),['state':'closed'])
            result.goToUrl(api.web.open(foundedSC[0]))
        }
    }
    else
    {
        return result.showMessage('Ошибка', "Заявка с темой \"${SHORTDESCR}\" не найдена.")
    }
}
else
{ return result.showMessage('Ошибка', "Действие недоступно для суперпользователя")}
