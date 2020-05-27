update message_template
set message = E'В Україні всього зареєстровано <b>%d</b> заражених людей (%s <b>%d</b> за добу), з яких \n<b>%d</b> одужали (%s <b>%d</b> за добу), \n<b>%d</b> померло (%s <b>%d</b> за добу), \n<b>%d</b> ще хворіють (%s <b>%d</b> за добу).'
where code = 'statistic_ukraine_d_diff';